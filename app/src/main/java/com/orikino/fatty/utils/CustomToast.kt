package com.orikino.fatty.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.orikino.fatty.R
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ItemCustomToastBinding


class CustomToast(private val context: Context, private val message: String,private val status : Boolean) : Toast(context) {

    lateinit var _binding: ItemCustomToastBinding

    fun createCustomToast() {
        _binding = ItemCustomToastBinding.inflate(LayoutInflater.from(context))


        _binding.toastMsg.text = message

        if (status) {
            _binding.root.background = ContextCompat.getDrawable(FattyApp.getInstance(),R.drawable.bg_toast_black)
            _binding.ivToast.setImageResource(R.drawable.fatty_circle_new)
        } else {
            _binding.root.background = ContextCompat.getDrawable(FattyApp.getInstance(),R.drawable.bg_toast_red)
            _binding.ivToast.setImageResource(R.drawable.ic_toast_error_24dp)
        }

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = _binding.root
        //toast.gravity = Gravity.BOTTOM
        toast.show()
    }



}