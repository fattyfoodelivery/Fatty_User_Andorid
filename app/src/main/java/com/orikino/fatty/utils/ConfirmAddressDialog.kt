package com.orikino.fatty.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.orikino.fatty.databinding.ItemConfirmDialogBinding

class ConfirmAddressDialog private constructor(ctx: Context, private val title : String="", private val message: String = "", private val btn : String ="",private val cancelCallback: () -> Unit = {}, private val callback: () -> Unit = {}) : DialogFragment() {

    private lateinit var itemConfirmDialogBinding: ItemConfirmDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        itemConfirmDialogBinding = ItemConfirmDialogBinding.inflate(inflater,container,false)

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
        return itemConfirmDialogBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemConfirmDialogBinding.tvTitle.text = title
        itemConfirmDialogBinding.tvDesc.text = message
        itemConfirmDialogBinding.btnConfirm.text = btn
        //tv_desc.text = message
        //tv_title.text = title
        //btnLogin.text = btn

        /*btnLogin.setOnClickListener {
            dismiss()
            callback.invoke()
        }*/

        itemConfirmDialogBinding.ivClose.setOnClickListener {
            dismiss()
        }

        itemConfirmDialogBinding.btnCancel.setOnClickListener {
            dismiss()
            cancelCallback.invoke()
        }

        itemConfirmDialogBinding.btnConfirm.setOnClickListener {
            dismiss()
            callback.invoke()
        }
    }

    companion object {
        fun Builder(ctx: Context, title:String, message: String, btn : String,cancelCallback: () -> Unit, callback: () -> Unit) = ConfirmAddressDialog(ctx, title,message,btn, callback)
    }
}