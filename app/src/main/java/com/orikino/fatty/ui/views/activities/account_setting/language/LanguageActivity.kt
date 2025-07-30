package com.orikino.fatty.ui.views.activities.account_setting.language

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.orikino.fatty.R
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityLanguageBinding
import com.orikino.fatty.ui.views.activities.base.MainActivity
import com.orikino.fatty.ui.views.activities.intro.IntroActivity
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.forceUpdateLocale

class LanguageActivity : AppCompatActivity() {

    lateinit var languageBinding: ActivityLanguageBinding

    companion object {
        const val CHANGE = "change"
        const val FROM_INTRO = "from-intro"

        fun getIntent(): Intent {
            return Intent(FattyApp.getInstance(), LanguageActivity::class.java)
        }
    }

    private  var languageValue : String = "en"
    private var change = false
    private var fromIntro = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        languageBinding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(languageBinding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.fattyPrimary)

        change = intent.getBooleanExtra(CHANGE, false)
        fromIntro = intent.getBooleanExtra(FROM_INTRO, false)


        check()
        languageSpinnerSetup()
        navigateToIntroScreen()
    }

    private fun navigateToIntroScreen() {
        languageBinding.btnGetStart.setOnClickListener {
            MainActivity.isFirstTime = true
            PreferenceUtils.readLanguage()?.let { it1 -> chooseLanguage(it1) }
        }
    }

    private fun chooseLanguage(language: String) {
        this.forceUpdateLocale(language) {
            finishAffinity()
        }
        finishAffinity()
        when {
            fromIntro && change -> startActivity(LanguageActivity.getIntent())
            change -> {
                //startActivity<SplashActivity>()
                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)

            }//startActivity(SplashActivity.getIntent())
            else -> startActivity(IntroActivity.getIntent())
        }
    }

    private fun languageSpinnerSetup() {
        checkLanguage()
        languageBinding.llEng.setOnClickListener {
            chooseEnglish()
            //CustomToast(this, "Successfully change language English", true).createCustomToast()
        }

        languageBinding.llMm.setOnClickListener {
            chooseMyanmar()
            //CustomToast(this, "Successfully change language Myanmar", true).createCustomToast()
        }

        languageBinding.llChina.setOnClickListener {
            chooseChina()
            //CustomToast(this, "Successfully change language Chinese", true).createCustomToast()
        }
        if (change) checkLanguage()
    }

    private fun check() {
        if (change) languageBinding.btnGetStart.text = "Change Language"
    }

    private fun chooseChina() {
        languageBinding.llChina.setBackgroundResource(R.drawable.lang_selected_bg)
        languageBinding.imgChinaStatus.setImageResource(R.drawable.radio_check)
        languageBinding.llEng.setBackgroundResource(0)
        languageBinding.imgEngStatus.setImageResource(R.drawable.radio_uncheck)
        languageBinding.llMm.setBackgroundResource(0)
        languageBinding.imgMmStatus.setImageResource(R.drawable.radio_uncheck)
    }

    private fun chooseMyanmar() {
        languageBinding.llMm.setBackgroundResource(R.drawable.lang_selected_bg)
        languageBinding.imgMmStatus.setImageResource(R.drawable.radio_check)
        languageBinding.llEng.setBackgroundResource(0)
        languageBinding.imgEngStatus.setImageResource(R.drawable.radio_uncheck)
        languageBinding.llChina.setBackgroundResource(0)
        languageBinding.imgChinaStatus.setImageResource(R.drawable.radio_uncheck)

    }

    private fun chooseEnglish() {
        languageBinding.llEng.setBackgroundResource(R.drawable.lang_selected_bg)
        languageBinding.imgEngStatus.setImageResource(R.drawable.radio_check)
        languageBinding.llChina.setBackgroundResource(0)
        languageBinding.imgChinaStatus.setImageResource(R.drawable.radio_uncheck)
        languageBinding.llMm.setBackgroundResource(0)
        languageBinding.imgMmStatus.setImageResource(R.drawable.radio_uncheck)

    }

    private fun checkLanguage() {
        when (PreferenceUtils.readLanguage()) {
            "my" -> chooseMyanmar()
            "en" -> chooseEnglish()
            else -> chooseChina()
        }
    }
}
