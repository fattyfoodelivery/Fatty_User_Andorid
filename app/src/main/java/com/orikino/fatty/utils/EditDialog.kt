package com.orikino.fatty.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.orikino.fatty.databinding.ItemEditDialogBinding

class EditDialog private constructor(ctx: Context, private val title : String="", private val message: String = "", private val btn : String ="", private val callback: (String) -> Unit = {}) : DialogFragment() {

    private lateinit var itemEditDialogBinding: ItemEditDialogBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        itemEditDialogBinding = ItemEditDialogBinding.inflate(inflater,container,false)

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
        return itemEditDialogBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        itemEditDialogBinding.ivClear.setOnClickListener {
            itemEditDialogBinding.edtName.setText("")
            itemEditDialogBinding.btnChange.isClickable = false
        }

        itemEditDialogBinding.edtName.doAfterTextChanged {

            it?.let {
                if (it.isEmpty()) {
                    itemEditDialogBinding.btnChange.alpha = 0.3f
                    itemEditDialogBinding.btnChange.isClickable = false
                } else {
                    itemEditDialogBinding.btnChange.alpha = 1f
                    itemEditDialogBinding.btnChange.isClickable = true
                }
            }

        }

        itemEditDialogBinding.ivClose.setOnClickListener {
            dismiss()
        }

        itemEditDialogBinding.btnChange.setOnClickListener {
            dismiss()
            itemEditDialogBinding.edtName.text?.toString()?.let(callback)
        }
    }

    companion object {
        fun Builder(ctx: Context, title:String, message: String, btn : String, callback: (String) -> Unit) = EditDialog(ctx, title,message,btn, callback)
    }
}