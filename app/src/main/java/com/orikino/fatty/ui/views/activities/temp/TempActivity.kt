package com.orikino.fatty.ui.views.activities.temp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.orikino.fatty.databinding.ActivityTempBinding

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


}