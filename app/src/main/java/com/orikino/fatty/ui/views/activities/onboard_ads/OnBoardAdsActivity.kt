package com.orikino.fatty.ui.views.activities.onboard_ads

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.orikino.fatty.R
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityOnBoardAdsBinding
import com.orikino.fatty.ui.views.activities.account_setting.language.LanguageActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OnBoardAdsActivity : AppCompatActivity() {

    private lateinit var _binding : ActivityOnBoardAdsBinding

    companion object {
        fun getIntent() : Intent {
            return Intent(FattyApp.getInstance(),OnBoardAdsActivity::class.java)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = resources.getColor(R.color.onboard)

        _binding = ActivityOnBoardAdsBinding.inflate(layoutInflater)
        setContentView(_binding.root)


        _binding.ivSkip.setOnClickListener { startActivity(LanguageActivity.getIntent()) }
        lifecycleScope.launch {
            delay(5000)
            startActivity(LanguageActivity.getIntent())
        }
    }
}