package com.orikino.fatty.ui.views.activities.profile
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ActivityProfileBinding
import com.orikino.fatty.domain.model.CustomerVO
import com.orikino.fatty.domain.view_model.UserViewModel
import com.orikino.fatty.domain.viewstates.UpdateUserInfoViewState
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.base.MainActivity
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    lateinit var profileBinding: ActivityProfileBinding

    private val viewModel : UserViewModel by viewModels()

    private var customer : CustomerVO = CustomerVO()
    var userName : String = ""

    companion object {
        const val USER_ID = "user-id"
        const val USER_PH = "user-ph"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)

        subscribeUI()
        navigateToBaseActivity()
        disableButton()
        onBack()


    }

    private fun onBack() {
        profileBinding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun subscribeUI() {
        viewModel.viewState.observe(this) { render(it) }
    }

    private fun navigateToBaseActivity() {
        profileBinding.edtName.requestFocus()
        profileBinding.edtName.addTextChangedListener {
            userName = it?.toString()?.trim() ?: ""
            if (userName.isEmpty()) {
                disableButton()
            } else {
                enableButton()
                viewModel.userName = userName
            }
        }
        profileBinding.ivClear.setOnClickListener {
            profileBinding.edtName.text.clear()

        }
        profileBinding.btnGetStart.setOnClickListener {

            if (viewModel.userName.isNotEmpty()) PreferenceUtils.readUserVO().customer_phone.let { it1 ->
                PreferenceUtils.readUserVO().customer_id?.let { it2 ->
                    viewModel.updateUserInfo(
                        PreferenceUtils.readDeviceToken().toString(),
                        it2,
                        viewModel.userName,
                        it1,
                        viewModel.image,
                        PreferenceUtils.readDeviceToken().toString()
                    )
                }
            }
        }
    }

    private fun disableButton() {
        profileBinding.ivClear.gone()
        profileBinding.btnGetStart.alpha = 0.8f
        profileBinding.btnGetStart.isEnabled = false
    }
    private fun enableButton() {
        profileBinding.ivClear.show()
        profileBinding.btnGetStart.alpha = 1f
        profileBinding.btnGetStart.isEnabled = true
    }


    private fun render(state : UpdateUserInfoViewState) {
        when(state) {
            is UpdateUserInfoViewState.OnLoadingUpdateUserInfo -> renderOnLoadingUpdateUserInfo()
            is UpdateUserInfoViewState.OnSuccessUpdateUserInfo -> renderOnSuccessProfile(state)
            is UpdateUserInfoViewState.OnFailUpdateUserInfo -> renderOnFailUpdateUserInfo(state)
            else -> {}
        }
    }

    private fun renderOnLoadingUpdateUserInfo() {
        LoadingProgressDialog.showLoadingProgress(this@ProfileActivity)
    }

    private fun renderOnSuccessProfile(state : UpdateUserInfoViewState.OnSuccessUpdateUserInfo) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            PreferenceUtils.writeUserVO(state.data.data)
            //startActivity<MainActivity>()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun renderOnFailUpdateUserInfo(state: UpdateUserInfoViewState.OnFailUpdateUserInfo) {
        LoadingProgressDialog.hideLoadingProgress()
        when(state.message) {
            "Server Issue" -> showSnackBar("Server Issue")
            "Another Login" -> {
                // Another Login
                WarningDialog.Builder(this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        //startActivity<LoginActivity>()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    })
                    .show(supportFragmentManager, ProfileActivity::class.simpleName)
            }
            "Denied" -> {
                // DENIED
                WarningDialog.Builder(this,
                    resources.getString(R.string.maintain_title),
                    resources.getString(R.string.maintain_msg),
                    "OK",
                    callback = { finishAffinity() })
                    .show(supportFragmentManager, ProfileActivity::class.simpleName)
            }
            else -> {
                showSnackBar(state.message!!)
            }
        }
    }
    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }

}