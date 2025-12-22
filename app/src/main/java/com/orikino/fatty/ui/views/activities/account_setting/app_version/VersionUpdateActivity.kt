package com.orikino.fatty.ui.views.activities.account_setting.app_version

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import com.orikino.fatty.BuildConfig
import com.orikino.fatty.R
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityVersionUpdateBinding
import com.orikino.fatty.domain.view_model.AboutViewModel
import com.orikino.fatty.domain.viewstates.BaseViewState
import com.orikino.fatty.utils.CustomTimer
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.helper.fixCutoutOfEdgeToEdge
import com.orikino.fatty.utils.helper.gone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VersionUpdateActivity : AppCompatActivity() {

    lateinit var _binding : ActivityVersionUpdateBinding

    private val viewModel : AboutViewModel by viewModels()

    companion object {
        fun getIntent() : Intent {
            return Intent(FattyApp.getInstance(),VersionUpdateActivity::class.java)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityVersionUpdateBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(_binding.root)
        _binding.root.fixCutoutOfEdgeToEdge(_binding.root)

        viewModel.getVersionUpdate()
        setUpObserver()
        checkUpdate()
        onBack()
        autoUpdateVersion()
    }

    private fun checkUpdate() {
        viewModel.getVersionUpdate()
//        _binding.btnUpdate.setOnClickListener {
//            autoUpdateVersion()
//        }
    }

    private fun setUpObserver() {
        viewModel.versionViewState.observe(this, Observer{
            renderVersion(it)
        })
    }

    private fun renderVersion(state: BaseViewState) {
        when (state) {
            is BaseViewState.OnSuccessVersionUpdate -> {
                // viewModel.onBoardingAd()
                val apiVersionString = state.data.data?.current_version // As per your existing code structure
                val appVersionCode = BuildConfig.VERSION_CODE // Using VERSION_CODE for integer comparison

                var needsUpdate = false
                if (!apiVersionString.isNullOrEmpty()) {
                    try {
                        val apiVersionInt = apiVersionString.toInt()
                        Log.d("VersionCheck", "API version: $apiVersionInt, App version code: $appVersionCode")
                        if (appVersionCode < apiVersionInt) { // Compare integers
                            needsUpdate = true
                            Log.i("VersionCheck", "App version mismatch. App: $appVersionCode, API: $apiVersionInt. Update required.")
                            val isForceUpdate = state.data.data?.is_force_update ?: false
                            val updateLink = state.data.data?.link ?: ""
                            if (updateLink.isNotEmpty()) {
                                //viewModel.onBoardingAd()
                                showUpdateAvailable(link = updateLink)
                            } else {
                                Log.e("VersionCheck", "Update link is missing from API response.")
                                // If update link is missing, but update is needed, what to do?
                                // For now, proceed as if no update needed if link is missing.
                                needsUpdate = false // Revert needsUpdate if link is missing
                            }
                        } else {
                            Log.i("VersionCheck", "App version is up to date.")
                            doneUI()
                        }
                    } catch (e: NumberFormatException) {
                        Log.e("VersionCheck", "API version string is not a valid integer: $apiVersionString")
                        // If API version is not a valid number, assume no update or handle as an error
                    }
                } else {
                    Log.w("VersionCheck", "API version string is null or empty.")
                }
            }
            is BaseViewState.OnFailVersionUpdate -> {
                doneUI()
            }
            else -> {}
        }
    }

    private fun render(state : AboutViewModel) {
        when(state){
        }
    }

    private fun onBack() {
        _binding.ivBack.setOnClickListener { finish() }
    }

    private fun showUpdateAvailable(link: String){
        _binding.tvStatusTitle.text = resources.getString(R.string.new_version_is_available)
        _binding.tvStatusMsg.text = resources.getString(R.string.your_app_will_be_the_latest_version)
        _binding.btnUpdate.alpha = 1F
        _binding.btnUpdate.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, link.toUri())
            startActivity(intent)
        }
    }

    private fun autoUpdateVersion() {
        _binding.tvStatusTitle.text = resources.getString(R.string.please_wait)
        _binding.tvStatusMsg.text = resources.getString(R.string.process_finish_a_few_min)
        _binding.btnUpdate.alpha = 0.2F
    }


    private fun doneUI() {
        _binding.tvStatusTitle.text = resources.getString(R.string.all_update)
        _binding.tvStatusMsg.text = resources.getString(R.string.up_to_date)
        _binding.btnUpdate.gone()
        //customTimer?.cancelTimer()
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}