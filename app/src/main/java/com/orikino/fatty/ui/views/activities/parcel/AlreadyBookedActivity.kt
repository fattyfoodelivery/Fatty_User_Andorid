package com.orikino.fatty.ui.views.activities.parcel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orikino.fatty.databinding.ActivityParcelAlreadyBookedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlreadyBookedActivity: AppCompatActivity() {
    private lateinit var binding: ActivityParcelAlreadyBookedBinding
    private var region = ""
    companion object{
        const val REGION_EXTRA = "region_extra"
        fun getIntent(context: Context, region : String): Intent {
            return Intent(context, AlreadyBookedActivity::class.java).apply {
                putExtra(REGION_EXTRA, region)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParcelAlreadyBookedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        region = intent.getStringExtra(REGION_EXTRA) ?: ""
        initView()
    }

    private fun initView(){
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnContinue.setOnClickListener {
            startActivity(BookingPendingActivity.getIntent(this, region))
            finish()
        }
    }
}