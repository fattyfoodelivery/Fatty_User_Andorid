package com.joy.fattyfood.ui.views.activities.promotion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.joy.fattyfood.R
import com.joy.fattyfood.app.FattyApp

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
}