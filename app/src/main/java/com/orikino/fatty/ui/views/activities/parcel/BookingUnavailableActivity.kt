package com.orikino.fatty.ui.views.activities.parcel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ActivityBookingUnavailableBinding
import com.orikino.fatty.utils.helper.fixCutoutOfEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingUnavailableActivity: AppCompatActivity() {
    private lateinit var binding: ActivityBookingUnavailableBinding
    companion object{
        const val FROM_TIME = "FROM_TIME"
        const val TO_TIME = "TO_TIME"
        fun getIntent(context: Context, fromTime : String, toTime : String): Intent {
            return Intent(context, BookingUnavailableActivity::class.java).apply {
                putExtra(FROM_TIME, fromTime)
                putExtra(TO_TIME, toTime)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingUnavailableBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.root.fixCutoutOfEdgeToEdge(binding.root)
        initView()
    }

    private fun initView(){
        binding.tvParcelSubtitle.text = getString(R.string.txt_booking_unavailabel_desc, intent.getStringExtra(FROM_TIME), intent.getStringExtra(TO_TIME))
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnContinue.setOnClickListener {
            finish()
        }
    }
}