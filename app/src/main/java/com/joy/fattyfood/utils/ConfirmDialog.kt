package com.joy.fattyfood.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.joy.fattyfood.databinding.LayoutConfirmCancelDialogBinding

class ConfirmDialog private constructor(ctx: Context, private val title : String="", private val message: String = "", private val btn : String ="",private val callback: () -> Unit = {}) : DialogFragment() {

    private lateinit var dialogBind: LayoutConfirmCancelDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialogBind = LayoutConfirmCancelDialogBinding.inflate(inflater,container,false)

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
        return dialogBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogBind.tvTitle.text = title
        dialogBind.tvDesc.text = message
        dialogBind.btnContact.text = btn


        //tv_desc.text = message
        //tv_title.text = title
        //btnLogin.text = btn

        /*btnLogin.setOnClickListener {
            dismiss()
            callback.invoke()
        }*/

        dialogBind.ivClose.setOnClickListener {
            dismiss()
        }

        dialogBind.btnCancel.setOnClickListener {
            dismiss()

        }

        dialogBind.btnContact.setOnClickListener {
            dismiss()
            callback.invoke()
        }


    }

    companion object {
        fun Builder(ctx: Context, title:String, message: String, btn : String, callback: () -> Unit) = ConfirmDialog(ctx, title,message,btn, callback)
    }
}