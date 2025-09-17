package com.orikino.fatty.ui.views.activities.promotion

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.orikino.fatty.R
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.utils.LocaleHelper

class PromotionDetailActivity : AppCompatActivity() {


    companion object {
        fun getIntent() : Intent {
            return Intent(FattyApp.getInstance(),PromotionDetailActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotion_detail)
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}