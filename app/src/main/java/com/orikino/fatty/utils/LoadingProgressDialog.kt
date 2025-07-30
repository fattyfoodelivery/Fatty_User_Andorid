package com.orikino.fatty.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.orikino.fatty.R

object LoadingProgressDialog {
    private var loadingProgress: Dialog? = null

    fun showLoadingProgress(context: Context) {
        if(loadingProgress ==null)
            loadingProgress = Dialog(context)
        loadingProgress?.let {
            val view = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null)
            it.setContentView(view)
            it.setCancelable(false)
            it.show()
            it.window?.setBackgroundDrawable(null)
        }
    }

    fun hideLoadingProgress(){
        loadingProgress?.let {
            if (it.isShowing) {
                it.hide()
                it.dismiss()
                it.cancel()
                loadingProgress = null
            }
        }
    }
}