package com.orikino.fatty.ui.views.activities.rest_detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.orikino.fatty.R
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.FragmentPhotoViewBinding
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils

class PhotoViewActivity : AppCompatActivity() {
    private lateinit var binding : FragmentPhotoViewBinding
    companion object Companion {
        private const val ARG_IMAGE_URL = "arg_image_url"
        var image : String = ""
        fun newInstance(imageUrl: String): Intent {
            this.image = imageUrl
            return Intent(FattyApp.Companion.getInstance(), PhotoViewActivity::class.java)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentPhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    fun initView() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        image.let { url ->
            if (url.isNotEmpty()) {
                binding.ivPhoto.load(PreferenceUtils.IMAGE_URL.plus("/food/").plus(url)) {
                    error(R.drawable.food_default_icon)
                    placeholder(R.drawable.food_default_icon)
                }
            } else {
                // Optionally, load a default/error image if the URL is empty
                binding.ivPhoto.setImageResource(R.drawable.restaurant_default_img)
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}