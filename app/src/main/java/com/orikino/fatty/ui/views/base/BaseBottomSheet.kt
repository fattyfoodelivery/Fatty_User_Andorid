package com.orikino.fatty.ui.views.base

import android.R
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orikino.fatty.utils.helper.getScreenWidth

open class BaseBottomSheet<VM : BaseViewModel> : BottomSheetDialogFragment() {

    protected val loginDialogWidth by lazy { getScreenWidth(requireContext() , 0.25) }

    private var loadingDialog : AlertDialog? = null

    fun createView(@LayoutRes layout : Int) =
        LayoutInflater.from(requireContext()).inflate(layout , null)!!

    fun createCustomDialog(context : Context? , view : View? , gravity : Int , cancelable : Boolean) : AlertDialog {
        val dialog = AlertDialog.Builder(context).create()
        dialog.setView(view)
        val window = dialog.window
        window?.setGravity(gravity)
        window!!.setBackgroundDrawableResource(R.color.transparent)
        val layoutParams = window.attributes
        layoutParams.y = 40 // bottom margin
        window.attributes = layoutParams
        dialog.setCancelable(cancelable)
        return dialog
    }

    fun modifyWindowsParamsAndShow(dialog : AlertDialog , width : Int , height : Int) {
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()
//        dialog.window?.attributes = lWindowParams
        dialog.window!!.setLayout(width , height)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}