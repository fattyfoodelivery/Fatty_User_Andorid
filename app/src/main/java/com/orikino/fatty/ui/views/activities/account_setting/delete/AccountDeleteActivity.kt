package com.orikino.fatty.ui.views.activities.account_setting.delete

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.orikino.fatty.R
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityAccountDeleteBinding
import com.orikino.fatty.domain.model.CustomerVO
import com.orikino.fatty.domain.model.FoodMenuByRestaurantVO
import com.orikino.fatty.domain.model.ParcelInfoVO
import com.orikino.fatty.domain.model.ParcelSenderReceiverVO
import com.orikino.fatty.domain.view_model.AboutViewModel
import com.orikino.fatty.domain.viewstates.AboutViewState
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.utils.ConfirmDialog
import com.orikino.fatty.utils.CustomToast
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.isConnected
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.viewpod.ConnectionErrorViewPod
import com.orikino.fatty.utils.viewpod.ConnectionErrorViewPodDelegate
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class AccountDeleteActivity : AppCompatActivity() , ConnectionErrorViewPodDelegate {

    lateinit var _binding : ActivityAccountDeleteBinding

    private  var mConnectionViewPod : ConnectionErrorViewPod? = null

    private val viewModel : AboutViewModel by viewModels()

    companion object {
        fun getIntent() : Intent {
            return Intent(FattyApp.getInstance(),AccountDeleteActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityAccountDeleteBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.btnDelete.setOnClickListener {
            reload()
        }

        _binding.ivBack.setOnClickListener {
            finish()
        }
        setUpObserver()
    }

    private fun reload() {
        if (!isConnected()) {
            _binding.llContextView.gone()
            _binding.connectionViewPod.root.show()
            _binding.btnDelete.gone()
            setupConnectionErrorView("")
        } else {
            _binding.llContextView.show()
            _binding.btnDelete.show()
            _binding.connectionViewPod.root.gone()
            confirmDelete()
        }

    }


    private fun setupConnectionErrorView(e : String) {
        /*mConnectionViewPod = _binding.connectionViewPod as ConnectionErrorViewPod
        when (e) {
            "" -> {
            }
            "Failed" -> {
                mConnectionViewPod.setEmptyData(
                    title = "Oops!",
                    message = "It seems like the data app \nyou're looking for couldn't be found\n Please reload again.",
                    resources.getDrawable(R.drawable.ic_toast_error_24dp)
                )
            }
            "Connection Issue!" -> {
                mConnectionViewPod.setEmptyData(
                    title = resources.getString(R.string.no_internet),
                    message = resources.getString(R.string.please_check_internet),
                    resources.getDrawable(R.drawable.no_wifi_or_connection)
                )
            }
        }*/
        mConnectionViewPod?.setEmptyData(
            title = resources.getString(R.string.no_internet),
            message = resources.getString(R.string.please_check_internet_and_try_againg),
            resources.getDrawable(R.drawable.no_wifi_or_connection)
        )
        mConnectionViewPod?.setDelegate(this)
    }

    private fun setUpObserver() {
        viewModel.viewState.observe(this) {
            render(it)
        }
    }

    private fun render(state : AboutViewState) {
        when(state) {
            is AboutViewState.OnLoadingDelete -> {
                LoadingProgressDialog.showLoadingProgress(this)
            }
            is AboutViewState.OnSuccessDeleteAccount -> renderSuccess(state)
            is AboutViewState.OnFailDeleteAccount -> {

            }
            is AboutViewState.OnSuccessLogout -> renderSuccessLogout()
            is AboutViewState.OnFailLogout -> renderFailLogout()
            else -> {}
        }
    }

    private fun renderSuccess(state: AboutViewState.OnSuccessDeleteAccount){
        if (state.data.success){
            PreferenceUtils.readUserVO().customer_id?.let { viewModel.fetchLogout(it) }
        }else{
            LoadingProgressDialog.hideLoadingProgress()
            CustomToast(this,
                state.data.message,true).createCustomToast()
        }
    }

    private fun renderSuccessLogout() {
        LoadingProgressDialog.hideLoadingProgress()
        clearCache()
        CustomToast(FattyApp.getInstance(),
            resources.getString(R.string.delete_success),true).createCustomToast()
        finishAffinity()
        //requireContext().startActivity<SplashActivity>()
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
    }

    private fun renderFailLogout() {
        LoadingProgressDialog.hideLoadingProgress()
    }

    private fun clearCache() {
        PreferenceUtils.writeUserVO(CustomerVO())
        PreferenceUtils.writeFirstTime(true)
        PreferenceUtils.writeLanguage("en")
        PreferenceUtils.writeFoodOrderList(mutableListOf())
        PreferenceUtils.writeAddToCart(false)
        PreferenceUtils.writeRestaurant(FoodMenuByRestaurantVO())
        PreferenceUtils.writeSenderReceiver(ParcelSenderReceiverVO())
        PreferenceUtils.writeParcelInfo(ParcelInfoVO())
        PreferenceUtils.writeIsSelected(0)
    }

    private fun confirmDelete() {
        val title = resources.getString(R.string.acc_del_title)
        val des = resources.getString(R.string.acc_del_des)
        val btn = resources.getString(R.string.str_del)
        ConfirmDialog.Builder(FattyApp.getInstance(),title,des,btn,
            callback = {
               viewModel.deleteAccount()
              /*  */

            }
        ).show(supportFragmentManager,AccountDeleteActivity::class.java.simpleName)
    }

    override fun onTapTryAgain() {
        onResume()
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}