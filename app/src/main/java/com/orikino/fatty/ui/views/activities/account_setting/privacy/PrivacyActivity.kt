package com.orikino.fatty.ui.views.activities.account_setting.privacy

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.domain.viewstates.AboutViewState
import com.orikino.fatty.databinding.ActivityPrivacyBinding
import com.orikino.fatty.domain.view_model.AboutViewModel
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint
import java.net.URISyntaxException

@AndroidEntryPoint
class PrivacyActivity : AppCompatActivity() {

    lateinit var _binding : ActivityPrivacyBinding

    private val viewModel : AboutViewModel by viewModels()

    companion object {
        fun getIntent() : Intent {
            return Intent(FattyApp.getInstance(),PrivacyActivity::class.java)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(_binding.root)


        updateWebView()
        viewModel.fetchPrivacy()
        privacyPolicyObserver()

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

    private fun privacyPolicyObserver() {
        viewModel.viewState.observe(this) {
            render(it)
        }
    }

    private fun render(state : AboutViewState) {
        when(state) {
            is AboutViewState.OnLoadingPrivacyPolicy -> {
                LoadingProgressDialog.showLoadingProgress(this)
            }
            is AboutViewState.OnSuccessPrivacyPolicy -> renderSuccess(state)
            is AboutViewState.OnFailPrivacyPolicy -> {
                LoadingProgressDialog.hideLoadingProgress()
            }
            else -> {}
        }
    }

    private fun renderSuccess(state: AboutViewState.OnSuccessPrivacyPolicy) {
        LoadingProgressDialog.hideLoadingProgress()
        //_binding.tvLinkPrivacy.setHtml(state.data.data.body)
        //_binding.tvLinkPrivacy.setOnClickATagListener { widget, spannedText, href -> true }
        state.data.data.body?.let {
            _binding.webView.loadDataWithBaseURL(
                null,
                it+"<br>",
                "text/html",
                "UTF-8",
                null
            )
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