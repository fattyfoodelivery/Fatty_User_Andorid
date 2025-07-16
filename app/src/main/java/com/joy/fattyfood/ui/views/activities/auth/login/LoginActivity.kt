package com.joy.fattyfood.ui.views.activities.auth.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.joy.fattyfood.R
import com.joy.fattyfood.app.FattyApp
import com.joy.fattyfood.databinding.ActivityLoginBinding
import com.joy.fattyfood.databinding.ConfirmLoginAlertBinding
import com.joy.fattyfood.domain.model.CustomerVO
import com.joy.fattyfood.domain.view_model.AuthViewModel
import com.joy.fattyfood.domain.viewstates.AuthViewState
import com.joy.fattyfood.service.RegisterForPushNotificationsAsync
import com.joy.fattyfood.ui.views.activities.account_setting.language.LanguageActivity
import com.joy.fattyfood.ui.views.activities.account_setting.privacy.PrivacyActivity
import com.joy.fattyfood.ui.views.activities.account_setting.term_condition.TermAndConditionActivity
import com.joy.fattyfood.ui.views.activities.auth.verify.VerifyOTPActivity
import com.joy.fattyfood.ui.views.activities.base.MainActivity
import com.joy.fattyfood.ui.views.components.CustomSpinner
import com.joy.fattyfood.utils.CustomToast
import com.joy.fattyfood.utils.GpsTracker
import com.joy.fattyfood.utils.LoadingProgressDialog
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.WarningDialog
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.hideSoftKeyboard
import com.joy.fattyfood.utils.helper.show
import com.joy.fattyfood.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import me.pushy.sdk.Pushy
import me.pushy.sdk.util.exceptions.PushyException


@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), CustomSpinner.OnSpinnerEventsListener {

    lateinit var loginBinding: ActivityLoginBinding

    private var result = ""
    private var fromVT: String? = null
    private val viewModel: AuthViewModel by viewModels()

    companion object {
        const val FROM = "from"
        fun getIntent(from : String): Intent {
            val intent = Intent(FattyApp.getInstance(), LoginActivity::class.java)
            intent.putExtra(FROM,from)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.fattyPrimary)

        fromVT = intent.getStringExtra(FROM)


        MainActivity.isFirstTime = true
        setUpObserver()
        setUpPushy()
        setUpPhoneNo()
        typePhoneNo()
        navigateToPolicy()
        navigateToTermAndCondition()
        navigateToVerifyOTPScreen()
        languageSpinnerSetUp()

        loginBinding.edtMmCode.clearFocus()
        loginBinding.edtPhone.requestFocus()
        loginBinding.spinnerLang.setSpinnerEventsListener(this)


        loginBinding.edtPhone.doAfterTextChanged {
            it?.let {
                if (it.isEmpty()) {
                    disableBtn(loginBinding.btnContinue)
                } else {
                    enabledBtn(loginBinding.btnContinue)
                    typePhoneNo()
                }
            }
        }

        onBack()


    }

    private fun onBack() {
        loginBinding.imvBack.setOnClickListener { finish() }
    }

    private fun languageSpinnerSetUp() {
        /*val languages =
        val adapter = LanguageAdapter(this, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter*/
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun navigateToPolicy() {
        loginBinding.lbtnPrivacy.setOnClickListener {
            //startActivity(PrivacyActivity.getIntent())
            //startActivity<PrivacyActivity>()
            val intent = Intent(this,PrivacyActivity::class.java)
            startActivity(intent)
        }

    }

    private fun navigateToTermAndCondition() {
        loginBinding.lbtnTermsConditions.setOnClickListener {
            //startActivity(TermAndConditionActivity.getIntent())
            //startActivity<TermAndConditionActivity>()
            val intent = Intent(this,TermAndConditionActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setUpPushy() {
        try {
            Pushy.subscribe("fatty/news", applicationContext)
        } catch (e: PushyException) {
            e.printStackTrace()
        }

        if (!Pushy.isRegistered(this)) {
            RegisterForPushNotificationsAsync(this).execute()
        }
    }

    private fun typePhoneNo() {
        loginBinding.edtPhone.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (loginBinding.edtPhone.text?.length !in 5..11) {
                    CustomToast(this, resources.getString(R.string.edit), false).createCustomToast()
                }
                // showSnackBar(resources.getString(R.string.edit))
                hideSoftKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun navigateToVerifyOTPScreen() {
        loginBinding.btnContinue.setOnClickListener {
            verifyPhone()
        }

        loginBinding.tvSkip.setOnClickListener {
            MainActivity.isFirstTime = true
            //startActivity<MainActivity>()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()

        }

    }

    private fun verifyPhone() {
        if (loginBinding.edtPhone.text!!.isEmpty()) {
            Toast.makeText(this, resources.getString(R.string.phone_no_error), Toast.LENGTH_SHORT).show()
            disableBtn(loginBinding.btnContinue)
            return
        }

        if (loginBinding.edtPhone.text!!.length !in 7..9) {
            Toast.makeText(this, resources.getString(R.string.phone_no_error), Toast.LENGTH_SHORT).show()
            disableBtn(loginBinding.btnContinue)
            return
        }

        if (loginBinding.edtPhone.text.toString()[0] == '0') {
            disableBtn(loginBinding.btnContinue)
            Toast.makeText(this, "Do not start with 09", Toast.LENGTH_SHORT).show()
            return
        } else {
            result = "+959" + loginBinding.edtPhone.text.toString()
            enabledBtn(loginBinding.btnContinue)
            viewModel.requestPhoneOtp(result)
        }
    }

    private fun setUpObserver() {
        viewModel.viewState.observe(this) { subscribe(it)}
    }

    private fun disableBtn(btn : AppCompatButton) {
        btn.alpha = 0.5f
        btn.isEnabled = false
    }

    private fun enabledBtn(btn : AppCompatButton) {
        btn.alpha = 1f
        btn.isEnabled = true
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

    private fun subscribe(state: AuthViewState) {
        when (state) {
            is AuthViewState.LoadingRequestPhone ->  renderOnLoadingRequestPhoneOtp()
            is AuthViewState.OnSuccessRequestPhone -> requestPhoneSuccess(state)
            is AuthViewState.OnFailRequestPhone -> renderOnFailRequestPhoneOtp(state)


            is AuthViewState.LoadingResentOtp -> renderOnLoadingResendOtp()
            is AuthViewState.OnFailRequestResentOtp -> renderOnFailForceSendPhoneOtp(state)
            is AuthViewState.OnSuccessResentOtp -> resendRequestOtp(state)
        }
    }

    private fun renderOnLoadingResendOtp() {
        LoadingProgressDialog.showLoadingProgress(this)
    }
    private fun renderOnLoadingRequestPhoneOtp() {
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun requestPhoneSuccess(state: AuthViewState.OnSuccessRequestPhone) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            /*startActivity<VerifyOTPActivity>(
                VerifyOTPActivity.PHONE to result
            )*/
            val intent = Intent(this,VerifyOTPActivity::class.java)
            intent.putExtra(VerifyOTPActivity.PHONE,result)
            startActivity(intent)
        } else if (state.data.data.type == 1) {
            showConfirmLoginDialog(
                title = resources.getString(R.string.alert),
                message = state.data.message,
                type = 1
            )
        } else if (state.data.data.type == 2){
            showConfirmLoginDialog(
                title = resources.getString(R.string.already_login_title),
                message = resources.getString(R.string.already_login_msg),
                type = 2
            )
        }

    }

    private fun renderOnFailRequestPhoneOtp(state: AuthViewState.OnFailRequestPhone) {
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            "Connection Issue" -> showSnackBar(resources.getString(R.string.no_internet_title))

            "FAILED" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = { finishAffinity() })
                .show(supportFragmentManager, LoginActivity::class.simpleName)

            "DENIED" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = { finishAffinity() })
                .show(supportFragmentManager, LoginActivity::class.simpleName)

            "Another Login" -> {

            }

            else -> showSnackBar(state.message)

        }
    }

    private fun navigateToLanguageScreen() {
        if (PreferenceUtils.readFirstTimeUserVO() == true && PreferenceUtils.readUserVO() == CustomerVO()) {
            //startActivity<LanguageActivity>()
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
        } else {
            //startActivity<MainActivity>()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun resendRequestOtp(state: AuthViewState.OnSuccessResentOtp) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            //startActivity<VerifyOTPActivity>(VerifyOTPActivity.PHONE to result)
            val intent = Intent(this,VerifyOTPActivity::class.java)
            intent.putExtra(VerifyOTPActivity.PHONE,result)
            startActivity(intent)
        }
    }

    private fun renderOnFailForceSendPhoneOtp(state: AuthViewState.OnFailRequestResentOtp) {
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            "Connection Issue" -> showSnackBar(resources.getString(R.string.no_internet_title))
            "DENIED" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = { finishAffinity() })
                .show(supportFragmentManager, LoginActivity::class.simpleName)
            else -> showSnackBar(state.message)
        }
    }


    private fun showConfirmLoginDialog(title: String, message: String, type: Int) {
        val _binding = ConfirmLoginAlertBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this@LoginActivity)
        builder.setView(_binding.root)
        builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            _binding.tvTitle.text = title
            _binding.tvTitleDesc.text = message
            if (type == 1) {
                _binding.llAnotherView.gone()
                _binding.btnOk.show()

            } else if (type == 2) {
                _binding.llAnotherView.show()
                _binding.btnOk.gone()
            }

            _binding.btnOk.setOnClickListener {
                dismiss()
            }
            _binding.btnClose.setOnClickListener {
                dismiss()
            }
            _binding.btnRemove.text = resources.getString(R.string.force_login)
            _binding.btnRemove.setOnClickListener {
                dismiss()
                viewModel.resendRequest(result)
            }
            show()
        }
    }


    private fun setUpPhoneNo() {
        if (fromVT == "base") loginBinding.tvSkip.gone()
        else loginBinding.tvSkip.show()
        loginBinding.edtPhone.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                loginBinding.scrollView.postDelayed(
                    Runnable { loginBinding.scrollView.fullScroll(ScrollView.FOCUS_DOWN) }, 200
                )
            }
        }
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onPopupWindowOpened(spinner: Spinner?) {
        loginBinding.spinnerLang.background = resources.getDrawable(R.drawable.bg_lang_spinner_down)
    }

    override fun onPopupWindowClosed(spinner: Spinner?) {
        loginBinding.spinnerLang.background = resources.getDrawable(R.drawable.bg_lang_spinner_up)
    }
}
