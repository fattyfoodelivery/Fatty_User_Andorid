package com.joy.fattyfood.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.joy.fattyfood.databinding.LayoutLoginDialogBinding


class WarningDialog private constructor(ctx: Context, private val title : String="", private val message: String = "",private val btn : String ="", private val callback: () -> Unit = {}) : DialogFragment() {

    private lateinit var loginDialogBinding: LayoutLoginDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        loginDialogBinding = LayoutLoginDialogBinding.inflate(inflater,container,false)

        dialog?.let { dialog ->
            dialog.window?.let {
                it.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            dialog.setCanceledOnTouchOutside(false)
        }
        return loginDialogBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginDialogBinding.tvTitle.text = title
        loginDialogBinding.tvDesc.text = message
        loginDialogBinding.btnLogin.text = btn

        loginDialogBinding.btnLogin.setOnClickListener {
            dismiss()
            callback.invoke()
        }
    }

    companion object {
        fun Builder(ctx: Context, title:String, message: String, btn : String, callback: () -> Unit) = WarningDialog(ctx, title,message,btn, callback)
    }
}