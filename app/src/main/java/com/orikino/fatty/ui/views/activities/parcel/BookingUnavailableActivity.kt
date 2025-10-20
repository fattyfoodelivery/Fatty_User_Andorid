package com.orikino.fatty.ui.views.activities.parcel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orikino.fatty.databinding.ActivityBookingUnavailableBinding

class BookingUnavailableActivity: AppCompatActivity() {
    private lateinit var binding: ActivityBookingUnavailableBinding
    companion object{
        fun getIntent(context: Context): Intent {
            return Intent(context, BookingSuccessActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingUnavailableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView(){
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnContinue.setOnClickListener {
            finish()
        }
    }
}