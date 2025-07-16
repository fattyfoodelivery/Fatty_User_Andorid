package com.joy.fattyfood.ui.views.activities.account_setting.delete

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.joy.fattyfood.R
import com.joy.fattyfood.app.FattyApp
import com.joy.fattyfood.databinding.ActivityAccountDeleteBinding
import com.joy.fattyfood.ui.views.activities.auth.login.LoginActivity
import com.joy.fattyfood.utils.ConfirmDialog
import com.joy.fattyfood.utils.CustomToast
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.isConnected
import com.joy.fattyfood.utils.helper.show
import com.joy.fattyfood.utils.viewpod.ConnectionErrorViewPod
import com.joy.fattyfood.utils.viewpod.ConnectionErrorViewPodDelegate

class AccountDeleteActivity : AppCompatActivity() , ConnectionErrorViewPodDelegate {

    lateinit var _binding : ActivityAccountDeleteBinding

    private  var mConnectionViewPod : ConnectionErrorViewPod? = null


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


    private fun confirmDelete() {
        val title = resources.getString(R.string.acc_del_title)
        val des = resources.getString(R.string.acc_del_des)
        val btn = resources.getString(R.string.str_del)
        ConfirmDialog.Builder(FattyApp.getInstance(),title,des,btn,
            callback = {
                _binding.progress.root.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    _binding.progress.root.gone()
                    CustomToast(FattyApp.getInstance(),
                        resources.getString(R.string.delete_success),true).createCustomToast()
                }, 2000)
                startActivity(LoginActivity.getIntent(

                    "delete"
                ))
                finish()

            }
        ).show(supportFragmentManager,AccountDeleteActivity::class.java.simpleName)
    }

    override fun onTapTryAgain() {
        onResume()
    }


}