package com.orikino.fatty.ui.views.activities.parcel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ActivityPendingBookedParcelBinding
import com.orikino.fatty.utils.helper.fixCutoutOfEdgeToEdge
import com.orikino.fatty.utils.helper.getCurrentDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingPendingActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPendingBookedParcelBinding
    private var region = ""
    companion object{
        const val REGION_EXTRA = "region_extra"
        fun getIntent(context: Context, region : String): Intent {
            return Intent(context, BookingPendingActivity::class.java).apply {
                putExtra(REGION_EXTRA, region)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingBookedParcelBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.root.fixCutoutOfEdgeToEdge(binding.root)
        region = intent.getStringExtra(REGION_EXTRA) ?: ""
        binding.tvDate.text = getCurrentDate()
        binding.tvRegion.text = region
        initView()
    }

    private fun initView(){
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}