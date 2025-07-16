package com.joy.fattyfood.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.joy.fattyfood.databinding.LayoutAccountRestrictedDialogBinding


class AccountRestrictedDialog private constructor(ctx: Context,private val callback: () -> Unit = {}) : DialogFragment() {


    lateinit var binding : LayoutAccountRestrictedDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutAccountRestrictedDialogBinding.inflate(inflater, container, false)
        dialog?.let { dialog ->
            dialog.window?.let {
                it.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCancel.setOnClickListener {
            dismiss()

        }
        binding.btnContact.setOnClickListener {
            dismiss()
            callback.invoke()
        }
    }

    companion object {
        fun Builder(ctx: Context, callback: () -> Unit) = AccountRestrictedDialog(ctx,callback)
    }
}