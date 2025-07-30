package com.orikino.fatty.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.orikino.fatty.databinding.LayoutLoginDialogBinding

class SuccessDialog private constructor(ctx: Context, private val message: String = "", private val callback: () -> Unit = {}) : DialogFragment() {

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
        }
        return loginDialogBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginDialogBinding.tvDesc.text = message
        loginDialogBinding.btnLogin.setOnClickListener {
            dismiss()
            callback.invoke()
        }
    }

    companion object {
        fun Builder(ctx: Context, message: String, callback: () -> Unit) = SuccessDialog(ctx, message, callback)
    }
}