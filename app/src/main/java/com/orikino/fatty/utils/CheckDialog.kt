package com.orikino.fatty.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ItemCheckDialogBinding

class CheckDialog(var callback: (Int) -> Unit) : DialogFragment() {

    private lateinit var itemCheckDialogBinding: ItemCheckDialogBinding

    var is_L_check : Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        itemCheckDialogBinding = ItemCheckDialogBinding.inflate(inflater,container,false)

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
        return itemCheckDialogBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemCheckDialogBinding.ivClose.setOnClickListener {
            dismiss()
        }

        var newpos = 0

        val lastselected = 1//PreferenceUtils.readCurrencyId()?.currency_id

        if (lastselected == 1) {
            lashioChecked()
        } else {
            museChecked()
        }




        itemCheckDialogBinding.rlLashio.setOnClickListener {
            lashioChecked()
            newpos = 1
        }

        itemCheckDialogBinding.rlMuse.setOnClickListener {
            museChecked()
            newpos = 2
        }

        itemCheckDialogBinding.btnConfirm.setOnClickListener {
            dismiss()
            callback.invoke(newpos)
        }

    }

    private fun lashioChecked () {
        itemCheckDialogBinding.ivCheckLashio.setImageResource(R.drawable.ic_circle_check)
        itemCheckDialogBinding.ivCheckMuse.setImageResource(R.drawable.radio_button_unchecked)
        itemCheckDialogBinding.rlLashio.setBackgroundResource(R.drawable.region_selectd_bg)
        itemCheckDialogBinding.rlMuse.setBackgroundResource(R.drawable.bg_region_choice)
    }

    private fun museChecked() {
        itemCheckDialogBinding.ivCheckMuse.setImageResource(R.drawable.ic_circle_check)
        itemCheckDialogBinding.ivCheckLashio.setImageResource(R.drawable.radio_button_unchecked)
        itemCheckDialogBinding.rlMuse.setBackgroundResource(R.drawable.region_selectd_bg)
        itemCheckDialogBinding.rlLashio.setBackgroundResource(R.drawable.bg_region_choice)
    }

}