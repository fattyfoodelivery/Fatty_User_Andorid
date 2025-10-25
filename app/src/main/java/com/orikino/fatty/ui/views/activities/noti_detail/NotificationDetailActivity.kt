package com.orikino.fatty.ui.views.activities.noti_detail

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
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ActivityNotificationDetailBinding
import com.orikino.fatty.domain.responses.SystemNotificationVO
import com.orikino.fatty.domain.view_model.NotiViewModel
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.fragments.NotiFragment
import com.orikino.fatty.utils.Constants
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import java.net.URISyntaxException

@AndroidEntryPoint
class NotificationDetailActivity : AppCompatActivity() {

    private val viewModel : NotiViewModel by viewModels()
    private lateinit var binding : ActivityNotificationDetailBinding
    private var notiId : Int = 0
    private var notiBody : String? = ""
    private var notiTitle : String? = null

    companion object {
        const val NOTI_ID = "noti_id"
        const val NOTI_BODY = "noti_body"
        const val NOTI_TITLE = "noti_title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notiId = intent.getIntExtra(NOTI_ID,0)
        notiBody = PreferenceUtils.readTempNotiBody()
        notiTitle = intent.getStringExtra(NOTI_TITLE)
        if (notiTitle != null) {
            binding.tvTitle.text = notiTitle
        }
        notiBody?.let { bindBody(it) }
//        if (notiBody?.isEmpty() == true) {
//            setUpObserver()
//        } else {
//
//        }
        setUpObserver()
        updateWebView()
        onBack()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun updateWebView() {
        binding.webView.settings.apply {
            javaScriptEnabled = true
            builtInZoomControls = false
            setSupportZoom(false)
            displayZoomControls = false

        }
        binding.webView.setBackgroundColor(Color.TRANSPARENT)
        binding.webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)

        binding.webView.webChromeClient = object : WebChromeClient() {}

        binding.webView.webViewClient = object : WebViewClient() {

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



            }
        }



    }

    private fun onBack() {
        binding.ivBack.setOnClickListener { onBackPressed() }
    }


    private fun setUpObserver() {
        //viewModel.fetchSystemNot(1,"")
        //viewModel.viewState.observe(this) { render(it) }
    }

    private fun render(state : com.orikino.fatty.domain.viewstates.InboxViewState) {
        when(state) {
            is com.orikino.fatty.domain.viewstates.InboxViewState.OnLoadingSystemNotiList -> renderOnLoadingSystemNotiList()
            is com.orikino.fatty.domain.viewstates.InboxViewState.OnSuccessSystemNotiList -> renderOnSuccessSystemNotiList(state)
            is com.orikino.fatty.domain.viewstates.InboxViewState.OnFailSystemNotiList -> renderOnFailSystemNotiList(state)
            else -> {}
        }
    }


    private fun renderOnLoadingSystemNotiList() {
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun renderOnSuccessSystemNotiList(state: com.orikino.fatty.domain.viewstates.InboxViewState.OnSuccessSystemNotiList) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            bindUI(state.data.data.notification.data)
        }
    }

    private fun renderOnFailSystemNotiList(state: com.orikino.fatty.domain.viewstates.InboxViewState.OnFailSystemNotiList) {
        LoadingProgressDialog.hideLoadingProgress()
        when(state.message){
            Constants.CONNECTION_ISSUE -> {
                showSnackBar(resources.getString(R.string.no_internet_title))
            }

            Constants.ANOTHER_LOGIN -> startActivity(LoginActivity.getIntent("noti"))


            Constants.DENIED -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                })
                .show(supportFragmentManager, NotiFragment::class.simpleName)

            Constants.FAILED -> {
                showSnackBar(state.message)
            }
            else -> {
                showSnackBar(state.message)
            }
        }
    }

    private fun bindUI(data : MutableList<SystemNotificationVO>) {
        for (msg in data) {
            if (msg.id == notiId) {
                notiBody = msg.body
            }
        }

        //notiBody?.let { binding.tvSystemNotiBody.setHtml(it) }
        //binding.tvSystemNotiBody.setOnClickATagListener { widget, spannedText, href -> true }
    }


    private fun bindBody(body : String) {
        //binding.tvSystemNotiBody.setHtml(body)
        //binding.tvSystemNotiBody.setOnClickATagListener { widget, spannedText, href -> true }
        body?.let {
            binding.webView.loadDataWithBaseURL(
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