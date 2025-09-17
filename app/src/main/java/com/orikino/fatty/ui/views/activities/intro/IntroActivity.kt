package com.orikino.fatty.ui.views.activities.intro

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.orikino.fatty.R
import com.orikino.fatty.adapters.IntroSlideAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityIntroBinding
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show


class IntroActivity : AppCompatActivity() {

    lateinit var introBinding: ActivityIntroBinding

    private var position = 0
    private lateinit var introSlideAdapter: IntroSlideAdapter

    companion object {
        fun getIntent() : Intent {
            return Intent(FattyApp.getInstance(),IntroActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        introBinding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(introBinding.root)

        prepareUi()
        navigateToLoginView()
        onBackPress()

    }


    private fun onBackPress() {
        introBinding.imvBack.setOnClickListener {
            startActivity(LoginActivity.getIntent("intro"))
        }
    }

    private fun prepareUi() {
        val numberOfScreens = 3
        introSlideAdapter = IntroSlideAdapter(this, numberOfScreens)
        introBinding.introViewpager.adapter = introSlideAdapter
        introBinding.introViewpager.isUserInputEnabled = true
        introBinding.introViewpager.registerOnPageChangeCallback(onBoardingPageChangeCallback)
        onBoardingPageChangeByClick()
    }

    private fun navigateToLoginView() {
        introBinding.imvSkip.setOnClickListener {

            //startActivity<LoginActivity>()
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private val onBoardingPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            updateCircleMarker(position)
        }
    }

    private fun onBoardingPageChangeByClick() {
        introBinding.btnNext.setOnClickListener {
            when(this.position){
                0 -> {
                    updateCircleMarker(0)
                    this.position +=1
                    introBinding.introViewpager.setCurrentItem(this.position, true)
                }
                1 -> {
                    updateCircleMarker(1)
                    this.position +=1
                    introBinding.introViewpager.setCurrentItem(this.position, true)
                }
                else -> {
                    updateCircleMarker(2)
                    introBinding.introViewpager.setCurrentItem(this.position, true)
                    //startActivity<LoginActivity>()
                    val intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                }
            }
        }
    }

    private fun updateCircleMarker(position: Int) {
        when (position) {
            0 -> {
                this.position = position
                introBinding.tvTitle.show()
                introBinding.llHorizontal.gone()
                introBinding.tvTitleColor.show()
                introBinding.tvTitle.text = resources.getString(R.string.intro_title_one)
                introBinding.tvTitleColor.text = resources.getString(R.string.intro_near_you)
                introBinding.tvDes.text = resources.getString(R.string.intro_dec_one)
                introBinding.introViewpager.setCurrentItem(position, true)
                firstSelected()
            }
            1 -> {
                this.position = position
                introBinding.llHorizontal.show()
                introBinding.tvTitleColor.gone()
                introBinding.tvTitleHorizontalColor.text = resources.getString(R.string.intro_track)
                introBinding.tvTitleHorizontal.text = resources.getString(R.string.intro_your_parcel)
                introBinding.tvTitle.text = resources.getString(R.string.intro_instantly)
                introBinding.tvDes.text = resources.getString(R.string.intro_dec_one)
                introBinding.introViewpager.setCurrentItem(position, true)
                secondSelected()
            }
            2 -> {
                this.position = position
                introBinding.tvTitle.show()
                introBinding.llHorizontal.gone()
                introBinding.tvTitleColor.show()
                introBinding.tvTitle.text = resources.getString(R.string.intro_get_any_where)
                introBinding.tvTitleColor.text = resources.getString(R.string.intro_in_hour)
                introBinding.tvDes.text = resources.getString(R.string.intro_dec_one)
                introBinding.btnNext.text = resources.getString(R.string.get_started)
                introBinding.introViewpager.setCurrentItem(position, true)
                thirdSelected()
            }
        }
    }

    private fun firstSelected() {
        introBinding.onBoardingInitialCircle.background =
            ContextCompat.getDrawable(this, R.drawable.selected_dot)
        introBinding.onBoardingMiddleCircle.background =
            ContextCompat.getDrawable(this, R.drawable.unselected_dot)
        introBinding.onBoardingThirdCircle.background =
            ContextCompat.getDrawable(this, R.drawable.unselected_dot)
    }


    private fun secondSelected() {
        introBinding.onBoardingInitialCircle.background =
            ContextCompat.getDrawable(this, R.drawable.unselected_dot)
        introBinding.onBoardingMiddleCircle.background =
            ContextCompat.getDrawable(this, R.drawable.selected_dot)
        introBinding.onBoardingThirdCircle.background =
            ContextCompat.getDrawable(this, R.drawable.unselected_dot)
    }

    private fun thirdSelected() {
        introBinding.onBoardingInitialCircle.background =
            ContextCompat.getDrawable(this, R.drawable.unselected_dot)
        introBinding.onBoardingMiddleCircle.background =
            ContextCompat.getDrawable(this, R.drawable.unselected_dot)
        introBinding.onBoardingThirdCircle.background =
            ContextCompat.getDrawable(this, R.drawable.selected_dot)
    }
    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }

}