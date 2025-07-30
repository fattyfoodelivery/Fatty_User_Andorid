package com.orikino.fatty.ui.views.activities.splash

import android.Manifest
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
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

    lateinit var countDownTimer: CountDownTimer
    private var isCountdownRunning = false

    companion object {
        private const val GOOGLE_PLAY_STORE_PACKAGE = "com.android.vending"
        const val timerMills: Long = 5000
        /*fun getIntent(): Intent {
            return Intent(FattyApp.getInstance(), SplashActivity::class.java)
        }*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)


        provider = LocationGooglePlayServicesProvider()
        provider?.setCheckLocationSettings(true)



        // TODO unrelease
        //PreferenceUtils.clearCache()
        println("Customer VO ${PreferenceUtils.readUserVO()}")

        setUpPushy()
        MainActivity.isFirstTime = true
        correctLocale()
        subScribeUI()
        skipToAds()
        splashBinding.flSplash.show()
        splashBinding.imvAds.gone()
        // configureSplashScreenImage()



        println("Pushy Device Auth Key")

        println("Customer Info")
    }

    override fun onResume() {
        super.onResume()
        setUpPermission()
        //checkConnection()
        // showBatteryOptimizationsWhitelistDialog()
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
            isCountdownRunning = true
            navigateToLanguageScreen()
        }
    }

    private fun navigateToLanguageScreen() {
        if (PreferenceUtils.readFirstTimeUserVO() == true && PreferenceUtils.readUserVO() == CustomerVO()) {
            //startActivity<LanguageActivity>()
            val intent = Intent(this,LanguageActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            //startActivity<MainActivity>()
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
            startCountdown(5000)
            splashBinding.imvAds.load(state.data.data?.image) {
                error(R.drawable.on_board_ads)
                placeholder(R.drawable.on_board_ads)
            }
        }
    }

    private fun renderOnBoardAdFail(state: SplashViewState.OnBoardAdFail) {
        LoadingProgressDialog.hideLoadingProgress()
        splashBinding.flSplash.gone()
        splashBinding.imvAds.gone()
        startCountdown(5000)
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

    /*private fun configureSplashScreenImage() {
        splashBinding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.fattyPrimary))
        when (PreferenceUtils.readLanguage()) {
            "my" -> splashBinding.ivAds.loadPhoto(R.drawable.on_board_ads)
            "en" -> splashBinding.ivAds.loadPhoto(R.drawable.on_board_ads)
            else -> splashBinding.ivAds.loadPhoto(R.drawable.on_board_ads)
        }
    }*/

    private fun showBatteryOptimizationsWhitelistDialog() {
        // Ensure device is already registered for notifications
        if (!Pushy.isRegistered(this)) {
            return
        }

        // Android M (6) and up only

        // Get power manager instance
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager

        // Check if app is already whitelisted from battery optimizations
        if (powerManager.isIgnoringBatteryOptimizations(packageName)) {
            return
        }

        // Instruct user to whitelist app from battery optimizations
        android.app.AlertDialog.Builder(this)
            .setTitle("Disable battery optimizations")
            .setMessage(
                this.resources.getString(
                    to_receive_notifications_in_the_background_please_set_battery_to_unrestricted_in_the_next_screen
                )
            )
            .setPositiveButton("OK") { dialogInterface, i -> // Open settings screen for this app
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)

                // Set package to current package
                intent.setData(Uri.fromParts("package", packageName, null))

                // Start settings activity
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null).show()
    }

    private fun showNoInternetDialog(title: String, message: String, delete: String) {
        val dialogBinding: LayoutLoginDialogBinding = LayoutLoginDialogBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this@SplashActivity)
        builder.setView(dialogBinding.root)
        alertDialog = builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            dialogBinding.tvTitle.text = title
            dialogBinding.tvDesc.text = message
            dialogBinding.btnLogin.text = delete

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
                            viewModel.onBoardingAd()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    /* ... */
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    private fun startCountdown(millis: Long) {
        countDownTimer = object : CountDownTimer(millis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                splashBinding.tvSkip.text = getString(R.string.skip).plus(" ").plus(seconds)
            }

            override fun onFinish() {
                if (!isCountdownRunning) {
                    navigateToLanguageScreen()
                }
            }
        }.start()

    }

    private fun cancelCountdown() {
        countDownTimer.cancel()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 112) {
            navigateToLanguageScreen()
        }
    }

    override fun onLocationUpdated(p0: Location?) {
    }
    override fun onStop() {
        super.onStop()
        smartLocation?.location(provider)?.oneFix()?.stop()
    }
}
