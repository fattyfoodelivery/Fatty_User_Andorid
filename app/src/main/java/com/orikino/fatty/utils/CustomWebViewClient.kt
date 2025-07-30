package com.orikino.fatty.utils

import android.app.Activity
import android.graphics.Bitmap
import android.webkit.*


open class CustomWebViewClient(private val delegate: CustomWebViewClientDelegate): WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        delegate.startLoading()
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        delegate.stopLoading()

    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url.toString().startsWith("http://159.223.66.158/fatty/main/admin/term&condition")) {
            delegate.finishWebView(Activity.RESULT_OK)
        }
        return true
    }
}

interface CustomWebViewClientDelegate {
    fun startLoading()
    fun stopLoading()

    fun finishWebView(resultCode: Int)
}