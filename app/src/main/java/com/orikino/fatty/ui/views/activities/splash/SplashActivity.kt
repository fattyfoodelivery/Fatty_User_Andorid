package com.orikino.fatty.ui.views.activities.splash

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import coil.load
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.orikino.fatty.R
import com.orikino.fatty.R.string.to_receive_notifications_in_the_background_please_set_battery_to_unrestricted_in_the_next_screen
import com.orikino.fatty.databinding.ActivitySplashBinding
import com.orikino.fatty.databinding.LayoutLoginDialogBinding
import com.orikino.fatty.domain.model.CustomerVO
import com.orikino.fatty.service.RegisterForPushNotificationsAsync
import com.orikino.fatty.domain.view_model.SplashViewModel
import com.orikino.fatty.ui.views.activities.account_setting.language.LanguageActivity
import com.orikino.fatty.ui.views.activities.base.MainActivity
import com.orikino.fatty.domain.viewstates.SplashViewState
import com.orikino.fatty.ui.views.activities.rest_detail.RestaurantDetailViewActivity
import com.orikino.fatty.ui.views.activities.webview.WebviewActivity
import com.orikino.fatty.utils.Constants.CONNECTION_ISSUE
import com.orikino.fatty.utils.Constants.DENIED
import com.orikino.fatty.utils.Constants.FAILED
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.correctLocale
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.isConnected
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import com.orikino.fatty.utils.helper.toDefaultRestaurantName
import dagger.hilt.android.AndroidEntryPoint
import io.nlopez.smartlocation.OnLocationUpdatedListener
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import me.pushy.sdk.Pushy

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() , OnLocationUpdatedListener {

    private lateinit var splashBinding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    private var provider: LocationGooglePlayServicesProvider? = null
    private var smartLocation: SmartLocation? = null

    private var alertDialog: AlertDialog? = null

    // --- Resumable Countdown Timer Variables ---
    private var countDownTimerInstance: CountDownTimer? = null
    private var timeRemainingWhenPaused: Long = timerMills // Initialize with full duration
    private var isTimerCancelledForNavigation: Boolean = false
    // --- End Resumable Countdown Timer Variables ---


    companion object {
        private const val GOOGLE_PLAY_STORE_PACKAGE = "com.android.vending"
        const val timerMills: Long = 5000L // Ensure it's Long
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        provider = LocationGooglePlayServicesProvider()
        provider?.setCheckLocationSettings(true)

        //PreferenceUtils.clearCache()
        println("Customer VO ${PreferenceUtils.readUserVO()}")

        setUpPushy()
        MainActivity.isFirstTime = true
        correctLocale()
        subScribeUI()
        skipToAds() // Setup skip button listener

        splashBinding.flSplash.show()
        splashBinding.imvAds.gone()

        println("Pushy Device Auth Key")
        println("Customer Info")
        // viewModel.onBoardingAd() is called in setUpPermission if permissions are granted
    }

    private fun startNewCountdown(durationMillis: Long) {
        countDownTimerInstance?.cancel() // Cancel any existing timer

        timeRemainingWhenPaused = durationMillis
        isTimerCancelledForNavigation = false // Reset this flag when a new countdown starts

        countDownTimerInstance = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemainingWhenPaused = millisUntilFinished
                val seconds = millisUntilFinished / 1000
                splashBinding.tvSkip.text = getString(R.string.skip).plus(" ").plus(seconds)
            }

            override fun onFinish() {
                timeRemainingWhenPaused = 0L
                splashBinding.tvSkip.text = getString(R.string.skip)
                countDownTimerInstance = null // Timer is done
                if (!isTimerCancelledForNavigation) { // Only navigate if not cancelled for prior navigation
                    navigateToLanguageScreen()
                }
            }
        }.start()
    }

    private fun cancelTimer(){
        countDownTimerInstance?.cancel()
        countDownTimerInstance = null
    }

    private fun cancelTimerAndNavigate(navigateToLanguageWithAdsParams: Pair<Boolean, Int?> = Pair(false, null)) {
        isTimerCancelledForNavigation = true // Mark that cancellation is for navigation
        countDownTimerInstance?.cancel()
        countDownTimerInstance = null
        timeRemainingWhenPaused = 0L // No need to resume if we are navigating away

        val (shouldNavigateWithAds, restaurantId) = navigateToLanguageWithAdsParams
        if (shouldNavigateWithAds && restaurantId != null) {
            navigateToLanguageScreenWithAds(restaurantId)
        } else {
            navigateToLanguageScreen()
        }
    }


    override fun onPause() {
        super.onPause()
        if (countDownTimerInstance != null && !isTimerCancelledForNavigation && !isChangingConfigurations) {
            // Pause the timer if it's running and not being cancelled for navigation,
            // and the activity is not being recreated due to configuration change.
            countDownTimerInstance?.cancel()
            // timeRemainingWhenPaused already holds the latest value from onTick
            countDownTimerInstance = null // So onResume knows to create a new one if needed
        }
    }

    override fun onResume() {
        super.onResume()
        setUpPermission() // This might trigger viewModel.onBoardingAd() which could lead to starting the countdown
        //checkConnection()
        // showBatteryOptimizationsWhitelistDialog()

        if (!isTimerCancelledForNavigation && timeRemainingWhenPaused > 0 && countDownTimerInstance == null) {
            // If the timer wasn't cancelled for navigation, there's time left,
            // and no timer instance is currently running (e.g., was paused), resume it.
            startNewCountdown(timeRemainingWhenPaused)
        }
        // If isTimerCancelledForNavigation is true, it means we've already decided to navigate,
        // so onResume shouldn't restart the timer.
    }


    private fun checkConnection() {
        if (this.isConnected()) {
            setUpPermission()
        } else {
            showNoInternetDialog(
                resources.getString(R.string.no_internet_title),
                resources.getString(R.string.no_internet),
                "OK"
            )
        }
    }

    private fun skipToAds() {
        splashBinding.tvSkip.setOnClickListener {
            cancelTimerAndNavigate()
        }
    }

    private fun navigateToLanguageScreenWithAds(restaurantID : Int) {
        if (PreferenceUtils.readFirstTimeUserVO() == true && PreferenceUtils.readUserVO() == CustomerVO()) {
            val intent = Intent(this,LanguageActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            startActivity(MainActivity.getIntentWithFlag(this, restaurantID))
            finish()
        }
    }

    private fun navigateToLanguageScreen() {
        if (PreferenceUtils.readFirstTimeUserVO() == true && PreferenceUtils.readUserVO() == CustomerVO()) {
            val intent = Intent(this,LanguageActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun subScribeUI() {
        viewModel.viewState.observe(this, Observer {
            render(it)
        })
    }

    private fun render(state: SplashViewState) {
        when (state) {
            is SplashViewState.OnLoadingOnBoardAds -> renderOnLoadingOnBoardAds()
            is SplashViewState.OnBoardAdSuccess -> renderOnBoardAdSuccess(state)
            is SplashViewState.OnBoardAdFail -> renderOnBoardAdFail(state)
        }
    }

    private fun renderOnLoadingOnBoardAds() {
        LoadingProgressDialog.showLoadingProgress(this@SplashActivity)
    }

    private fun renderOnBoardAdSuccess(state: SplashViewState.OnBoardAdSuccess) {
        splashBinding.imvAds.show()
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            if (countDownTimerInstance == null && !isTimerCancelledForNavigation) { // Start only if not already running/navigating
                startNewCountdown(timerMills)
            }
            splashBinding.imvAds.load(state.data.data?.image) {
                error(R.drawable.on_board_ads) // Ensure you have this drawable
                placeholder(R.drawable.on_board_ads) // Ensure you have this drawable
            }
            splashBinding.imvAds.setOnClickListener {

                when(state.data.data?.displayTypeId){
                    1 -> {
                        cancelTimerAndNavigate(Pair(true, state.data.data?.restaurantId ?: 0))
                    }
                    2 -> {
                        cancelTimer()
                        val intent = WebviewActivity.getIntent(this,state.data.data?.restaurantName ?: "",state.data.data?.displayTypeDescription ?: "")
                        startActivity(intent)
                    }
                    3 -> {
                        cancelTimer()
                        val url = state.data.data?.displayTypeDescription ?: ""
                        if (url.isNotEmpty()) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                if (intent.resolveActivity(packageManager) != null) {
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this, "Cannot open link: No application can handle this request.", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(this, "Cannot open link: No browser found.", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(this, "Cannot open link: Invalid URL.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "No URL provided for this item.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else { // if !state.data.success, handle as a failure case, perhaps similar to renderOnBoardAdFail
            splashBinding.flSplash.gone()
            splashBinding.imvAds.gone()
            if (countDownTimerInstance == null && !isTimerCancelledForNavigation) { // Start only if not already running/navigating
                startNewCountdown(timerMills) // Start countdown even on ad load fail for consistent UX
            }
            showSnackBar(state.data.message ?: "Failed to load ad data.")
        }
    }

    private fun renderOnBoardAdFail(state: SplashViewState.OnBoardAdFail) {
        LoadingProgressDialog.hideLoadingProgress()
        splashBinding.flSplash.gone()
        splashBinding.imvAds.gone()

        if (countDownTimerInstance == null && !isTimerCancelledForNavigation) { // Start only if not already running/navigating
            startNewCountdown(timerMills) // Start countdown even on ad load fail for consistent UX
        }

        when(state.message) {
            CONNECTION_ISSUE -> {
                showSnackBar(resources.getString(R.string.no_internet))
            }

            DENIED -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()
                })
                .show(this.supportFragmentManager, SplashActivity::class.simpleName)

            FAILED -> { showSnackBar(state.message) }
            else -> {
                showSnackBar(state.message)
            }
        }
    }

    private fun setUpPushy() {
        if (!Pushy.isRegistered(this)) {
            RegisterForPushNotificationsAsync(this).execute()
        } else {
            Pushy.listen(this)
        }
    }

    private fun showBatteryOptimizationsWhitelistDialog() {
        if (!Pushy.isRegistered(this)) {
            return
        }
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        if (powerManager.isIgnoringBatteryOptimizations(packageName)) {
            return
        }
        android.app.AlertDialog.Builder(this)
            .setTitle("Disable battery optimizations")
            .setMessage(
                this.resources.getString(
                    to_receive_notifications_in_the_background_please_set_battery_to_unrestricted_in_the_next_screen
                )
            )
            .setPositiveButton("OK") { _, _ ->
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.setData(Uri.fromParts("package", packageName, null))
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null).show()
    }

    private fun showNoInternetDialog(title: String, message: String, deleteText: String) { // Renamed 'delete' to 'deleteText'
        val dialogBinding: LayoutLoginDialogBinding = LayoutLoginDialogBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this@SplashActivity)
        builder.setView(dialogBinding.root)
        alertDialog = builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            dialogBinding.tvTitle.text = title
            dialogBinding.tvDesc.text = message
            dialogBinding.btnLogin.text = deleteText // Use renamed parameter

            dialogBinding.btnLogin.setOnClickListener {
                dismiss()
            }
            show()
        }
    }

    private fun setUpPermission() {
        Dexter.withContext(this@SplashActivity)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.let {
                        if (it.areAllPermissionsGranted()) {
                            viewModel.onBoardingAd() // This will trigger render methods, which can start the countdown
                        } else {
                            // Permissions not granted, still might want a failsafe countdown
                            // or a different user experience.
                            // For now, onBoardingAd() won't be called, so ad-related countdown won't start.
                            // If a generic countdown is always needed, consider calling startNewCountdown here.
                            // However, the current logic starts countdown on AdSuccess/AdFail.
                            Toast.makeText(this@SplashActivity, "Location permission is required to show relevant ads.", Toast.LENGTH_LONG).show()
                            // Potentially start a generic countdown if ads can't be fetched
                            if (countDownTimerInstance == null && !isTimerCancelledForNavigation) {
                                startNewCountdown(timerMills)
                            }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    // Required by OnLocationUpdatedListener, but not used in current logic
    override fun onLocationUpdated(location: Location?) {}
}
