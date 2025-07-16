package com.joy.fattyfood.ui.views.activities.profile
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.ActivityProfileBinding
import com.joy.fattyfood.domain.model.CustomerVO
import com.joy.fattyfood.domain.view_model.UserViewModel
import com.joy.fattyfood.domain.viewstates.UpdateUserInfoViewState
import com.joy.fattyfood.ui.views.activities.auth.login.LoginActivity
import com.joy.fattyfood.ui.views.activities.base.MainActivity
import com.joy.fattyfood.utils.LoadingProgressDialog
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.WarningDialog
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.show
import com.joy.fattyfood.utils.helper.showSnackBar
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


}