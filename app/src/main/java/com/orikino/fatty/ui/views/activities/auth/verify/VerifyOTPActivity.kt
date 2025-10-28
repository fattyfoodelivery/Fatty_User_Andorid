package com.orikino.fatty.ui.views.activities.auth.verify

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ActivityVerifyOtpBinding
import com.orikino.fatty.service.RegisterForPushNotificationsAsync
import com.orikino.fatty.domain.view_model.OtpViewModel
import com.orikino.fatty.domain.viewstates.VerifyOtpState
import com.orikino.fatty.ui.views.activities.base.MainActivity
import com.orikino.fatty.ui.views.activities.profile.ProfileActivity
import com.orikino.fatty.utils.GpsTracker
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.setColorSpannable
import com.orikino.fatty.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import `in`.aabhasjindal.otptextview.OTPListener
import me.pushy.sdk.Pushy
import me.pushy.sdk.util.exceptions.PushyException

@AndroidEntryPoint
class VerifyOTPActivity : AppCompatActivity() {

    lateinit var verifyBinding: ActivityVerifyOtpBinding

    private val viewModel : OtpViewModel by viewModels()

    var resultPhone: String = ""
    private var smsCode: String = ""
    lateinit var countDownTimerOtp: CountDownTimer

    companion object {
        const val PHONE = "phone"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verifyBinding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(verifyBinding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.fattyPrimary)

        resultPhone = intent.getStringExtra(PHONE).toString()
        val otpSupString = getString(R.string.we_have_sent_a_6_digit_to, resultPhone)
        verifyBinding.tvOtpSendTo.text = otpSupString
        val otpSpanLength = resultPhone.length
        viewModel.phoneNo = resultPhone
        if (PreferenceUtils.readLanguage() == "en"){
            verifyBinding.tvOtpSendTo.text =
                setColorSpannable(otpSupString , ContextCompat.getColor(this , R.color.black) , 26 , 26 + otpSpanLength)
        }else if (PreferenceUtils.readLanguage() == "my"){
            verifyBinding.tvOtpSendTo.text =
                setColorSpannable(otpSupString , ContextCompat.getColor(this , R.color.black) , 0 , 0 + otpSpanLength)
        }else{
            verifyBinding.tvOtpSendTo.text =
                setColorSpannable(otpSupString , ContextCompat.getColor(this , R.color.black) , 13 , 13 + otpSpanLength)
        }

        setUpPushy()
        subscribeUI()
        startTimer()
        navigateToUpdateUserInfoScreen()
        verifyBinding.otpView.requestFocusOTP()
        verifyBinding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {}
            override fun onOTPComplete(otp: String) {
                smsCode = otp
                verifyBinding.scrollView.postDelayed(
                    Runnable { verifyBinding.scrollView.fullScroll(ScrollView.FOCUS_DOWN) }, 200
                )
            }
        }
        navigateToSkip()
        sendCodeAgain()
        onBack()
    }

    private fun onBack() {
        verifyBinding.imvBack.setOnClickListener {
            finish()
        }
    }


    private fun navigateToSkip() {
        verifyBinding.tvSkip.setOnClickListener {
            //startActivity<MainActivity>()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onResume() {
        super.onResume()
        checkGPS()
    }



    private fun checkGPS() {
        var gpsTracker = GpsTracker(this)
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert()
        }
    }

    private fun setUpPushy() {
        try {
            val deviceToken = Pushy.register(this)
            PreferenceUtils.writeDeviceToken(deviceToken)
            if (!Pushy.isRegistered(this)) {
                RegisterForPushNotificationsAsync(this).execute()
            }
            Pushy.subscribe("fatty/news", applicationContext)
        } catch (e: PushyException) {
            e.printStackTrace()
        }
    }

    private fun startTimer() {
        countDownTimerOtp = object : CountDownTimer(60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                verifyBinding.tvResendCode.text = "( $seconds )"
            }

            override fun onFinish() {
                verifyBinding.tvResendCode.text = resources.getString(R.string.resend_code)
                sendCodeAgain()
            }
        }.start()
    }

    private fun navigateToUpdateUserInfoScreen() {
        verifyBinding.btnContinue.setOnClickListener {
            if (smsCode != null) {
                authenticate()
            }
        }
    }

    private fun authenticate() {

        if (smsCode.length != 6) {
            Toast.makeText(this, resources.getString(R.string.fill_sms_code), Toast.LENGTH_SHORT)
                .show()
            return
        } else {
            viewModel.verifyOtp(resultPhone,PreferenceUtils.readDeviceToken().toString(),0,smsCode.toInt())
        }
    }

    private fun sendCodeAgain() {
        verifyBinding.tvResendCode.setOnClickListener {
            resultPhone.let {
                viewModel.resentForExpire(it)
            }
        }
    }
    private fun subscribeUI() {
        viewModel.viewState.observe(
            this,
            Observer {
                render(it)
            }
        )
    }

    private fun render(state: VerifyOtpState) {
        when (state) {
            is VerifyOtpState.OnLoadingVerify -> renderOnLoadingVerify()
            is VerifyOtpState.OnSuccessVerifyOtp -> renderOnSuccessVerifyOtp(state)
            is VerifyOtpState.OnFailVerifyOtp -> renderOnFailVerifyOtp(state)

            is VerifyOtpState.onLoadExpireRequest -> renderLoadingExpireRequest()
            is VerifyOtpState.OnSuccessExpireRequest -> renderOnSuccessExpireRequest(state)
            is VerifyOtpState.OnFailExpireRequest -> renderOnFailExpireRequest(state)
        }

    }

    private fun renderOnFailExpireRequest(state: VerifyOtpState.OnFailExpireRequest) {
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            "Server Issue" ->
                showSnackBar("Server Error ${state.message}")
            "Denied" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = { finishAffinity() })
                .show(supportFragmentManager, VerifyOTPActivity::class.simpleName)
            else -> showSnackBar(state.message!!)

        }
    }

    private fun renderOnSuccessExpireRequest(state: VerifyOtpState.OnSuccessExpireRequest) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            startTimer()
            showSnackBar(getString(R.string.otp_send_msg, resultPhone))
        }
    }

    private fun renderLoadingExpireRequest() {
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun renderOnFailVerifyOtp(state: VerifyOtpState.OnFailVerifyOtp) {
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            "Server Issue" ->
                showSnackBar("Server Error")
            "Denied" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = { finishAffinity() })
                .show(supportFragmentManager, VerifyOTPActivity::class.simpleName)
            else -> showSnackBar(state.message!!)

        }
    }

    private fun renderOnSuccessVerifyOtp(state: VerifyOtpState.OnSuccessVerifyOtp) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            PreferenceUtils.writeFirstTime(false)
            PreferenceUtils.writeUserVO(state.data.data.customer)
            if (state.data.data.is_old) {
                //startActivity<MainActivity>()
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                //startActivity<ProfileActivity>()
                val intent = Intent(this,ProfileActivity::class.java)
                startActivity(intent)
                finish()
            }
        }else{
            showSnackBar(state.data.message)
        }

    }




    private fun renderOnLoadingVerify() {
       LoadingProgressDialog.showLoadingProgress(this)
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}
