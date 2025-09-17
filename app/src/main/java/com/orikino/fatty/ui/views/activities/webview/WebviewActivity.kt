package com.orikino.fatty.ui.views.activities.webview

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.orikino.fatty.databinding.ActivityWebviewBinding
import java.net.URISyntaxException
import androidx.core.net.toUri
import com.orikino.fatty.utils.LocaleHelper

class WebviewActivity : AppCompatActivity() {
    private lateinit var binding : ActivityWebviewBinding
    private var title : String = ""
    private var body : String = ""

    companion object{
        const val TITLE = "title"
        const val BODY = "body"
        fun getIntent(context: Context, title : String, body : String) = Intent(context, WebviewActivity::class.java).apply {
            putExtra(TITLE,title)
            putExtra(BODY,body)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = intent.getStringExtra(TITLE) ?: ""
        body = intent.getStringExtra(BODY) ?: ""
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.tvWebview.text = title
        setUpWebView()
        binding.webView.loadDataWithBaseURL(
            null, body, "text/html", "UTF-8", null
        )
    }

    private fun setUpWebView() {
        binding.webView.settings.apply {
            javaScriptEnabled = true
            builtInZoomControls = false
            setSupportZoom(false)
            displayZoomControls = false
        }
        binding.webView.setBackgroundColor(Color.TRANSPARENT)
        binding.webView.webChromeClient = WebChromeClient()
        // Custom WebViewClient to handle URL loading, phone numbers, emails, etc.
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (request != null) {
                    val url = request.url.toString()
                    if (url.startsWith("tel:")) {
                        val intent = Intent(Intent.ACTION_DIAL, url.toUri())
                        startActivity(intent)
                        return true
                    } else if (url.startsWith("mailto:")) {
                        val intent = Intent(Intent.ACTION_SENDTO, url.toUri())
                        startActivity(intent)
                        return true
                    } else if (!url.contains("t.me")) {
                        val url = request.url.toString()
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Open in external browser
                        startActivity(intent)
                        return true
                    } else if (url.startsWith("geo:")) {
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        intent.setPackage("com.google.android.apps.maps")  // Ensures Google Maps is used
                        startActivity(intent)
                        return true
                    } else if (url.startsWith("https://wa.me/") || url.contains("whatsapp://")) {
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        intent.setPackage("com.whatsapp")  // Ensure it opens in WhatsApp
                        startActivity(intent)
                        return true
                    } else if (url.startsWith("market:") || url.contains("play.google.com")) {
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        intent.setPackage("com.android.vending")  // Ensure Google Play Store opens
                        startActivity(intent)
                        return true
                    } else if (url.startsWith("intent:")) {
                        try {
                            val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                            startActivity(intent)
                            return true
                        } catch (e: URISyntaxException) {
                            e.printStackTrace()
                        }
                    } else if (url.startsWith("ftp:")) {
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        startActivity(intent)
                        return true
                    } else if (url.startsWith("sms:")) {
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        startActivity(intent)
                        return true
                    } else {
                        view?.loadUrl(request.url.toString())
                    }
                }
                return true
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                request?.takeIf { it.isForMainFrame }?.let {
                    val url = it.url.toString()
                    if (binding.webView.copyBackForwardList().currentIndex > 0) {
                        binding.webView.goBack()
                    } else {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}