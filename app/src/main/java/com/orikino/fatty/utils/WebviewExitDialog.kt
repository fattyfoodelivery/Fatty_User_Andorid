package com.orikino.fatty.utils

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.orikino.fatty.R
import com.orikino.fatty.databinding.LayoutConfirmCancelDialogBinding
import com.orikino.fatty.utils.helper.gone

class WebviewExitDialog private constructor(
    ctx: Context,
    private val title: String = "",
    private val message: String = "",
    private val btn: String = "",
    private val callback: () -> Unit = {},
    private val dismissCallback: () -> Unit? = {}
) : DialogFragment() {

    private lateinit var dialogBind: LayoutConfirmCancelDialogBinding
    private var isHideCancelBtn = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialogBind = LayoutConfirmCancelDialogBinding.inflate(inflater, container, false)

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
        dialogBind.btnCancel.text = getString(R.string.txt_webview_cancel)

        //tv_desc.text = message
        //tv_title.text = title
        //btnLogin.text = btn

        /*btnLogin.setOnClickListener {
            dismiss()
            callback.invoke()
        }*/

        if (isHideCancelBtn) {
            dialogBind.btnCancel.visibility = View.GONE
        }
        dialogBind.ivClose.gone()
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissCallback.invoke()
    }

    fun hideCancelBtn() {
        isHideCancelBtn = true
    }


    companion object {
        fun Builder(
            ctx: Context,
            title: String,
            message: String,
            btn: String,
            callback: () -> Unit,
            dismissCallback: () -> Unit? = {}
        ) = WebviewExitDialog(ctx, title, message, btn, callback, dismissCallback)
    }
}