package com.orikino.fatty.ui.views.activities.promotion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.orikino.fatty.R
import com.orikino.fatty.app.FattyApp

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