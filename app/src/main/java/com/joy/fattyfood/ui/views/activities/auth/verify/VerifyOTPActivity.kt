package com.joy.fattyfood.ui.views.activities.auth.verify

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.ActivityVerifyOtpBinding
import com.joy.fattyfood.service.RegisterForPushNotificationsAsync
import com.joy.fattyfood.domain.view_model.OtpViewModel
import com.joy.fattyfood.domain.viewstates.VerifyOtpState
import com.joy.fattyfood.ui.views.activities.base.MainActivity
import com.joy.fattyfood.ui.views.activities.profile.ProfileActivity
import com.joy.fattyfood.utils.GpsTracker
import com.joy.fattyfood.utils.LoadingProgressDialog
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.WarningDialog
import com.joy.fattyfood.utils.helper.showSnackBar
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
        verifyBinding.tvPhone.text = resultPhone
        viewModel.phoneNo = resultPhone


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
        countDownTimerOtp = object : CountDownTimer(30 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                verifyBinding.tvResendCode.text = "( $seconds )"
            }

            override fun onFinish() {
                verifyBinding.tvResendCode.text = resources.getString(R.string.send_again)
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
            showSnackBar("${resources.getString(R.string.otp_send_msg)}${resultPhone}")
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
        }

    }




    private fun renderOnLoadingVerify() {
       LoadingProgressDialog.showLoadingProgress(this)
    }

}
