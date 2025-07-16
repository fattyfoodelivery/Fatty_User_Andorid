package com.joy.fattyfood.ui.views.activities.account_setting.about

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import com.joy.fattyfood.app.FattyApp
import com.joy.fattyfood.databinding.ActivityAboutUsBinding
import com.joy.fattyfood.domain.view_model.AboutViewModel
import com.joy.fattyfood.domain.viewstates.AboutViewState
import dagger.hilt.android.AndroidEntryPoint
import java.net.URISyntaxException

@AndroidEntryPoint
class AboutUsActivity : AppCompatActivity() {

    lateinit var _binding : ActivityAboutUsBinding

    private val viewModel: AboutViewModel by viewModels()

    companion object {
        fun getIntent() : Intent {
            return Intent(FattyApp.getInstance(),AboutUsActivity::class.java)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        viewModel.fetchAboutApp()
        aboutObserver()
        updateWebView()
        onBack()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun updateWebView() {
        _binding.webView.settings.apply {
            javaScriptEnabled = true
            builtInZoomControls = false
            setSupportZoom(false)
            displayZoomControls = false

        }
        _binding.webView.setBackgroundColor(Color.TRANSPARENT)
        _binding.webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)

        _binding.webView.webChromeClient = object : WebChromeClient() {}

        _binding.webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                request?.url?.toString()?.let { url ->
                    println("term condition url $url")

                    // Determine the intent based on the URL
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
                            // Try Chrome first, fallback to default browser
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
                            return true // Load in WebView
                        }
                    }

                    // Launch the intent if possible
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
                /*view?.evaluateJavascript(
                    """ (function() { var meta = document.createElement('meta');
                         meta.name = 'viewport'; meta.content = 'width=device-width, initial-scale=1';
                         document.getElementsByTagName('head')[0].appendChild(meta); })();
                          """.trimIndent(), null )*/


                view?.evaluateJavascript(
                    """
            (function() {
                var phonePattern = /\b\d{9,11}\b/g;
                document.body.innerHTML = document.body.innerHTML.replace(phonePattern, function(phone) {
                    return '<a href="tel:' + phone + '">' + phone + '</a>';
                });
            })();
            """.trimIndent(), null
                )
            }
        }



    }

    private fun onBack() {
        _binding.ivBack.setOnClickListener { finish() }
    }

    private fun aboutObserver() {

        viewModel.viewState.observe(this) {
            render(it)
        }
    }

    private fun render(state : com.joy.fattyfood.domain.viewstates.AboutViewState) {
        when(state) {
            is com.joy.fattyfood.domain.viewstates.AboutViewState.OnLoadingAbout -> {
                renderLoading()
            }
            is com.joy.fattyfood.domain.viewstates.AboutViewState.OnSuccessAbout -> {
                renderSuccess(state)
            }
            is com.joy.fattyfood.domain.viewstates.AboutViewState.OnFailAbout -> {
                renderFail()
            }
            else -> {}

        }
    }

    private fun renderLoading() {

    }

    private fun renderSuccess(state: AboutViewState.OnSuccessAbout) {
        //_binding.tvAbout.setHtml(state.data.data.about)
        //_binding.tvAbout.setOnClickATagListener { widget, spannedText, href -> true }
        state.data.data.about?.let {
            _binding.webView.loadDataWithBaseURL(
                null,
                it+"<br>",
                "text/html",
                "UTF-8",
                null
            )
        }
    }

    private fun renderFail() {

    }
}