package com.orikino.fatty.ui.views.activities.account_setting.language

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orikino.fatty.R
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivitySettingLanguageBinding
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.forceUpdateLocale

class SettingLanguageActivity : AppCompatActivity(){

    lateinit var _binding: ActivitySettingLanguageBinding
    var langValue: String = ""
    var lngCode : String = ""

    private var change = false
    private var fromIntro = false

    companion object {
        const val CHANGE = "change"
        const val FROM_INTRO = "from-intro"

        fun getIntent(): Intent {
            return Intent(FattyApp.getInstance(), SettingLanguageActivity::class.java)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivitySettingLanguageBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        setUpLan()
        onBackFinish()
        languageSetUp()
        navigateToIntroScreen()



    }

    private fun setUpLan() {
        when(PreferenceUtils.readLanguage()) {
            "zh" -> chooseChina()
            "my" -> chooseMyanmar()
            else -> chooseEnglish()
        }
    }

    private fun onBackFinish() {
        _binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun languageSetUp() {
        _binding.apply {
            llEng.setOnClickListener {
                chooseEnglish()
                langValue = "English"
            }

            llMm.setOnClickListener {
                chooseMyanmar()
                langValue = "Myanmar"
            }

            llChina.setOnClickListener {
                chooseChina()
                langValue = "Chinese"
            }
        }
    }

    private fun chooseChina() {
        PreferenceUtils.writeLanguage("zh")
        _binding.llChina.setBackgroundResource(R.drawable.lang_selected_bg)
        _binding.imgChinaStatus.setImageResource(R.drawable.radio_check)
        _binding.llEng.setBackgroundResource(0)
        _binding.imgEngStatus.setImageResource(R.drawable.radio_uncheck)
        _binding.llMm.setBackgroundResource(0)
        _binding.imgMmStatus.setImageResource(R.drawable.radio_uncheck)
        lngCode = "zh"
    }

    private fun chooseMyanmar() {
        PreferenceUtils.writeLanguage("my")
        _binding.llMm.setBackgroundResource(R.drawable.lang_selected_bg)
        _binding.imgMmStatus.setImageResource(R.drawable.radio_check)
        _binding.llEng.setBackgroundResource(0)
        _binding.imgEngStatus.setImageResource(R.drawable.radio_uncheck)
        _binding.llChina.setBackgroundResource(0)
        _binding.imgChinaStatus.setImageResource(R.drawable.radio_uncheck)
        lngCode = "my"
    }

    private fun chooseEnglish() {
        PreferenceUtils.writeLanguage("en")
        _binding.llEng.setBackgroundResource(R.drawable.lang_selected_bg)
        _binding.imgEngStatus.setImageResource(R.drawable.radio_check)
        _binding.llChina.setBackgroundResource(0)
        _binding.imgChinaStatus.setImageResource(R.drawable.radio_uncheck)
        _binding.llMm.setBackgroundResource(0)
        _binding.imgMmStatus.setImageResource(R.drawable.radio_uncheck)
        lngCode = "en"
    }

    private fun navigateToIntroScreen() {
        _binding.btnChange.setOnClickListener {
            this.forceUpdateLocale(lngCode)
            //CustomToast(this, "Successfully change to $langValue", true).createCustomToast()
            //startActivity(SplashActivity.getIntent())
            //startActivity<SplashActivity>()
            val intent = Intent(this,SplashActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

}
