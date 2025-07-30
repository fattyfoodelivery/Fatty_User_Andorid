package com.orikino.fatty.ui.views.activities.account_setting.app_version

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.orikino.fatty.R
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityVersionUpdateBinding
import com.orikino.fatty.domain.view_model.AboutViewModel
import com.orikino.fatty.utils.CustomTimer
import com.orikino.fatty.utils.helper.gone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VersionUpdateActivity : AppCompatActivity() {

    lateinit var _binding : ActivityVersionUpdateBinding

    private val viewModel : AboutViewModel by viewModels()

    var customTimer: CustomTimer? = null

    companion object {
        fun getIntent() : Intent {
            return Intent(FattyApp.getInstance(),VersionUpdateActivity::class.java)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityVersionUpdateBinding.inflate(layoutInflater)
        setContentView(_binding.root)


        setUpObserver()
        checkUpdate()
        onBack()

    }

    private fun checkUpdate() {
        _binding.btnUpdate.setOnClickListener {
            autoUpdateVersion()
        }
    }

    private fun setUpObserver() {

    }

    private fun render(state : AboutViewModel) {
        when(state){
        }
    }

    private fun onBack() {
        _binding.ivBack.setOnClickListener { finish() }
    }

    private fun autoUpdateVersion() {
        _binding.tvStatusTitle.text = resources.getString(R.string.please_wait)
        _binding.tvStatusMsg.text = resources.getString(R.string.process_finish_a_few_min)
        _binding.btnUpdate.alpha = 0.2F
        Handler(Looper.getMainLooper()).postDelayed({
            doneUI()
        }, 3000)
        /*customTimer?.startTimer()

        _binding.tvStatusTitle.text = resources.getString(R.string.please_wait)
        _binding.tvStatusMsg.text = resources.getString(R.string.process_finish_a_few_min)
        _binding.btnUpdate.alpha = 0.2F

        customTimer?.startCoroutineTimer(30000,20000, callBack = {
            doneUI()
        })*/

    }


    private fun doneUI() {
        _binding.tvStatusTitle.text = resources.getString(R.string.all_update)
        _binding.tvStatusMsg.text = resources.getString(R.string.up_to_date)
        _binding.btnUpdate.gone()
        //customTimer?.cancelTimer()
    }


}