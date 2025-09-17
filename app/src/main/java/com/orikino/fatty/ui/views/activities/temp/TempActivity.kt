package com.orikino.fatty.ui.views.activities.temp

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.orikino.fatty.databinding.ActivityTempBinding
import com.orikino.fatty.utils.LocaleHelper

class TempActivity : AppCompatActivity() {


    private lateinit var bind : ActivityTempBinding

    companion object {
        const val isEdit = "is-edit"
        const val USER_INFO = "user-info"
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityTempBinding.inflate(layoutInflater)
        setContentView(bind.root)

    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}