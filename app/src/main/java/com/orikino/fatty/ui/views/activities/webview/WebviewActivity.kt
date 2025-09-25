package com.orikino.fatty.ui.views.activities.webview

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.orikino.fatty.databinding.ActivityWebviewBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URISyntaxException
import androidx.core.net.toUri
import com.orikino.fatty.utils.LocaleHelper

class WebviewActivity : AppCompatActivity() {
    private lateinit var binding : ActivityWebviewBinding
    private var title : String = ""
    private var bodyContent : String = ""
    private var bodyFilePath: String? = null

    companion object{
        const val TITLE = "title"
        const val BODY = "body" // For direct HTML content (legacy or small content)
        const val BODY_FILE_PATH = "body_file_path" // For HTML content from a file

        // Existing getIntent for backward compatibility or small direct content
        fun getIntent(context: Context, title : String, body : String) = Intent(context, WebviewActivity::class.java).apply {
            putExtra(TITLE,title)
            putExtra(BODY,body)
        }

        // New getIntent for large content via file path
        fun getIntentWithFilePath(context: Context, title : String, filePath : String) = Intent(context, WebviewActivity::class.java).apply {
            putExtra(TITLE,title)
            putExtra(BODY_FILE_PATH, filePath)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = intent.getStringExtra(TITLE) ?: ""
        bodyFilePath = intent.getStringExtra(BODY_FILE_PATH)

        bodyContent = if (bodyFilePath != null) {
            try {
                File(bodyFilePath!!).readText(Charsets.UTF_8)
            } catch (e: IOException) {
                Log.e("WebviewActivity", "Error reading HTML content from file: $bodyFilePath", e)
                "<html><body>Error loading content.</body></html>"
            }
        } else {
            // Fallback to reading directly from 'BODY' extra if file path is not provided
            intent.getStringExtra(BODY) ?: "<html><body>No content.</body></html>"
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.tvWebview.text = title
        setUpWebView()
        binding.webView.loadDataWithBaseURL(
            null, bodyContent, "text/html", "UTF-8", null
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        binding.webView.settings.apply {
            javaScriptEnabled = true
            builtInZoomControls = false
            setSupportZoom(false)
            displayZoomControls = false
        }
        binding.webView.setBackgroundColor(Color.TRANSPARENT)
        binding.webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)
        binding.webView.webChromeClient = WebChromeClient()
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                request?.url?.toString()?.let { url ->
                    println("term condition url $url")
                    val intent: Intent? = when {
                        url.startsWith("tel:") -> Intent(Intent.ACTION_DIAL, Uri.parse(url))
                        url.startsWith("mailto:") -> Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                        url.startsWith("geo:") -> Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        url.startsWith("https://wa.me/") || url.contains("whatsapp://") -> Intent(
                            Intent.ACTION_VIEW, Uri.parse(url)).apply {
                            setPackage("com.whatsapp")
                        }
                        url.startsWith("market:") || url.contains("play.google.com") -> Intent(
                            Intent.ACTION_VIEW, Uri.parse(url)).apply {
                            setPackage("com.android.vending")
                        }
                        url.startsWith("intent:") -> try {
                            Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        } catch (e: URISyntaxException) {
                            e.printStackTrace()
                            null
                        }
                        url.startsWith("ftp:") -> Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        url.startsWith("sms:") -> Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        url.startsWith("http://") || url.startsWith("https://") -> {
                            try {
                                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                    setPackage("com.android.chrome")
                                }
                            } catch (e: ActivityNotFoundException) {
                                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            }
                        }
                        else -> {
                            view?.loadUrl(url)
                            return true
                        }
                    }
                    intent?.let {
                        try {
                            startActivity(it)
                        } catch (e: ActivityNotFoundException) {
                            //showLogD("No application found to handle this link: ${e.message}")
                        }
                    }
                    return true
                }
                return false
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                if (request?.isForMainFrame == true) {
                    val url = request.url.toString()
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        //showLogD("Unable to open link: ${e.message}")
                    }
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up the temporary file if it was used
        bodyFilePath?.let { filePath -> // Changed 'it' to 'filePath' for clarity
            try {
                val file = File(filePath)
                if (file.exists()) {
                    val wasDeleted = file.delete() // Perform deletion
                    if (!wasDeleted) {
                        // Optionally log if deletion failed
                        Log.w("WebviewActivity", "Failed to delete temporary file: $filePath")
                    }else{
                        //why not adding else state is not working but empty else is work wtf?
                    }
                }else{
                    //why not adding else state is not working but empty else is work wtf?
                }
            } catch (e: Exception) {
                Log.e("WebviewActivity", "Error deleting temporary HTML file: $filePath", e)
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