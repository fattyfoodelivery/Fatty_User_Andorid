package com.orikino.fatty.ui.views.activities.services

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ActivityServiceShopWebviewBinding
import com.orikino.fatty.domain.viewstates.ServiceViewState
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.ui.views.fragments.HomeFragment
import com.orikino.fatty.utils.ConfirmDialog
import com.orikino.fatty.utils.CustomToast
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.fixCutoutOfEdgeToEdge
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServiceShopWebView : AppCompatActivity() {

    private lateinit var binding : ActivityServiceShopWebviewBinding

    private val viewModel : ServicesViewModel by viewModels()

    private var isLoaded : Boolean = false

    private var storeId : Int = 0
    private var storeName : String = ""

    companion object{
        const val STORE_ID = "STORE_ID"
        const val STORE_NAME = "STORE_NAME"

        fun getIntent(context: Context, storeId : Int, storeName : String) : Intent {
            return Intent(context, ServiceShopWebView::class.java).apply {
                putExtra(STORE_ID, storeId)
                putExtra(STORE_NAME, storeName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceShopWebviewBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.root.fixCutoutOfEdgeToEdge(binding.root)

        storeId = intent.getIntExtra(STORE_ID, 0)
        storeName = intent.getStringExtra(STORE_NAME) ?: ""

        binding.tvTitle.text = storeName

        // Clear cache and cookies before loading new session
        clearCacheAndCookies()

        setupWebView()

        this.onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPressed()
            }
        })
        binding.ivBack.setOnClickListener {
            exitConfirmDialog()
        }

        if (!isLoaded){
            PreferenceUtils.readUserVO().customer_id?.let {
                viewModel.fetchShopWebLink(storeId, it)
            }
        }

        listenToViewModel()
    }

    private fun exitConfirmDialog(){
        ConfirmDialog.Builder(
            this,
            getString(R.string.txt_are_you_sure_you_want_to_exit),
            getString(R.string.txt_exit_description),
            getString(R.string.txt_exit),
            callback = {
                clearCacheAndCookies()
                finish()
            })
            .show(
                supportFragmentManager,
                ServiceShopWebView::class.simpleName
            )
    }

    private fun listenToViewModel(){
        viewModel.viewState.observe(this){
            render(it)
        }
    }

    private fun render(state : ServiceViewState){
        when(state){
            is ServiceViewState.OnLoadingShopWebLink -> {onLoadingShopWebLink()}
            is ServiceViewState.OnSuccessShopWebLink -> {onSuccessShopWebLink(state)}
            is ServiceViewState.OnFailShopWebLink -> {onFailShopWebLink(state)}
            else -> {}
        }
    }

    private fun onLoadingShopWebLink(){
        loadingView(true)
    }

    private fun onSuccessShopWebLink(state : ServiceViewState.OnSuccessShopWebLink){
        loadWebUrl(state.data.data?.web_view_link ?: "")
//        val intent = CustomTabsIntent.Builder()
//            .setShowTitle(true) //
//            .setUrlBarHidingEnabled(true)
//            .build()
//        intent.launchUrl(this, Uri.parse(state.data.data?.web_view_link ?: ""))
//        isLoaded = true
    }

    private fun onFailShopWebLink(state : ServiceViewState.OnFailShopWebLink){
        loadingView(false)
        when (state.message) {
            "Server Error" -> {
                //binding.layoutNetworkError.root.show()
            }
            "Another Login" -> {
                WarningDialog.Builder(this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finishAffinity()
                        val intent = Intent(this,SplashActivity::class.java)
                        startActivity(intent)
                        //requireContext().startActivity<SplashActivity>()
                    }).show(supportFragmentManager, HomeFragment::class.simpleName)
            }

            "DENIED" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                }).show(supportFragmentManager, ServiceDetailActivity::class.simpleName)

            else ->
            {
                showSnackBar(state.message)
            }
        }
    }

    /**
     * Comprehensive cache and cookie clearing
     */
    private fun clearCacheAndCookies() {
        try {
            // Clear WebView cache
            binding.webView.clearCache(true)
            binding.webView.clearFormData()
            binding.webView.clearHistory()
            binding.webView.clearSslPreferences()

            // Clear cookies
            val cookieManager = CookieManager.getInstance()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.removeAllCookies { success ->
                    Log.d("WebViewFragment", "Cookies cleared: $success")
                }
                cookieManager.flush()
            } else {
                @Suppress("DEPRECATION")
                cookieManager.removeAllCookie()
                @Suppress("DEPRECATION")
                cookieManager.removeSessionCookie()
            }

            // Clear WebStorage (localStorage, sessionStorage, etc.)
            WebStorage.getInstance().deleteAllData()

            // Clear database storage
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                @Suppress("DEPRECATION")
                binding.webView.context.deleteDatabase("webview.db")
                @Suppress("DEPRECATION")
                binding.webView.context.deleteDatabase("webviewCache.db")
            }

            Log.d("WebViewFragment", "All cache and cookies cleared")
        } catch (e: Exception) {
            Log.e("WebViewFragment", "Error clearing cache", e)
        }
    }

    private fun handleBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            exitConfirmDialog()
        }
    }

    private fun setupWebView() {
        // Enable cookie management FIRST
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(binding.webView, true)
            flush()
        }

        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = false
                displayZoomControls = false
                setSupportZoom(false)
                cacheMode = WebSettings.LOAD_NO_CACHE
                databaseEnabled = true

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }

                javaScriptCanOpenWindowsAutomatically = true
                allowFileAccess = true
                allowContentAccess = true
                setSupportMultipleWindows(false)
                loadsImagesAutomatically = true
                blockNetworkImage = false
            }

//            // Add JavaScript interface for blob downloads
//            addJavascriptInterface(object : Any() {
//                @JavascriptInterface
//                fun getBase64FromBlobData(base64Data: String, filename: String, mimeType: String) {
//                    Log.d(
//                        "WebViewFragment",
//                        "Received blob data - filename: $filename, mime: $mimeType"
//                    )
//                    runOnUiThread {
//                        handleBase64Download(base64Data, filename, mimeType)
//                    }
//                }
//            }, "AndroidDownloader")

//            // Setup download listener
//            setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
//                handleDownload(url, userAgent, contentDisposition, mimetype, contentLength)
//            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url.toString()
                    Log.d("WebViewFragment", "shouldOverrideUrlLoading: $url")

//                    // Check if URL is a downloadable file
//                    if (isDownloadableUrl(url) && !url.startsWith("blob:")) {
//                        handleDownload(url, "", "", getMimeTypeFromUrl(url), 0)
//                        return true
//                    }

//                    if (url.endsWith("/login") || url.endsWith("/en/login")) {
//                        if (!url.contains("token=")) {
//                            Log.e("WebViewFragment", "Redirected to login - authentication failed")
//                            CustomToast(
//                                this@ServiceShopWebView,
//                                "Authentication failed. Please try again.",
//                                true
//                            ).createCustomToast()
//                            runOnUiThread {
//                                Handler(Looper.getMainLooper()).postDelayed({
//                                    clearCacheAndCookies()
//                                    finish()
//                                }, 1000)
//                            }
//                            return true
//                        }
//                    }

                    if (url.contains("accounts.google.com") ||
                        url.contains("oauth.google.com") ||
                        url.contains("facebook.com/login") ||
                        url.contains("twitter.com/oauth")
                    ) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            startActivity(intent)
                            return true
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    return false
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    loadingView(true)
                    Log.d("WebViewFragment", "Page started: $url")
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    loadingView(false)
                    Log.d("WebViewFragment", "Page finished: $url")
                    CookieManager.getInstance().flush()

                    // Inject blob download handler
                    //injectBlobDownloadScript()

                    if (url?.contains("/login") == true) {
                        Log.e("WebViewFragment", "Ended on login page - token may be invalid")
                    }
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    if (request?.isForMainFrame == true) {
                        loadingView(false)
                        Log.e(
                            "WebViewFragment",
                            "Error: ${error?.description}, Code: ${error?.errorCode}"
                        )
                        CustomToast(
                            this@ServiceShopWebView,
                            "Failed to load page",
                            true
                        ).createCustomToast()
                    }
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)
                    if (request?.isForMainFrame == true) {
                        Log.e(
                            "WebViewFragment",
                            "HTTP Error: ${errorResponse?.statusCode} for ${request.url}"
                        )
                    }
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    Log.e("WebViewFragment", "SSL Error: ${error?.toString()}")
                    handler?.cancel()
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
//                    binding.progressBar.progress = newProgress
//                    if (newProgress == 100) {
//                        binding.progressBar.hide()
//                    } else {
//                        binding.progressBar.show()
//                    }
                }

                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    consoleMessage?.let {
                        Log.d(
                            "WebViewConsole",
                            "${it.message()} -- Line ${it.lineNumber()} of ${it.sourceId()}"
                        )
                    }
                    return true
                }

//                override fun onShowFileChooser(
//                    webView: WebView?,
//                    filePathCallback: ValueCallback<Array<Uri>>?,
//                    fileChooserParams: FileChooserParams?
//                ): Boolean {
//                    Log.d("WebViewFragment", "onShowFileChooser called")
//
//                    // Cancel any existing callbacks
//                    this@WebViewFragment.filePathCallback?.onReceiveValue(null)
//                    this@WebViewFragment.filePathCallback = filePathCallback
//                    this@WebViewFragment.fileChooserParams = fileChooserParams
//
//                    // Check what type of files are accepted
//                    val acceptTypes = fileChooserParams?.acceptTypes
//                    val isImageOnly = acceptTypes?.all { it.startsWith("image/") } == true
//                    val isFileUpload = acceptTypes?.any {
//                        it.contains("application/") ||
//                                it.contains("*/*") ||
//                                it.contains(".pdf") ||
//                                it.contains(".doc")
//                    } == true
//
//                    Log.d("WebViewFragment", "Accept types: ${acceptTypes?.joinToString()}")
//                    Log.d("WebViewFragment", "Is image only: $isImageOnly, Is file upload: $isFileUpload")
//
//                    // Show appropriate dialog based on file type
//                    when {
//                        isImageOnly -> {
//                            // Camera icon clicked - show image upload options only
//                            if (checkRequiredPermissions()) {
//                                showImageSourceDialog()
//                            } else {
//                                requestRequiredPermissions()
//                            }
//                        }
//
//                        isFileUpload -> {
//                            // File icon clicked - show document picker only
//                            showFileSourceDialog()
//                        }
//
//                        else -> {
//                            // Fallback for unknown types
//                            showImageSourceDialog()
//                        }
//                    }
//
//                    return true
//                }
            }
        }
    }

    private fun loadWebUrl(webUrl : String) {
        if (webUrl.isEmpty()) {
            CustomToast(
                this,
                "Invalid URL",
                true
            ).createCustomToast()
            finish()
            return
        }

        Log.d("WebViewFragment", "Loading URL: $webUrl")

        val headers = mutableMapOf<String, String>()
        headers["User-Agent"] =
            "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
        headers["Accept"] =
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
        headers["Accept-Language"] = "en-US,en;q=0.9"
        headers["Cache-Control"] = "no-cache, no-store, must-revalidate"
        headers["Pragma"] = "no-cache"

        binding.webView.loadUrl(webUrl, headers)
    }

    private fun loadingView(status: Boolean) {
        if (status) {
            binding.layoutProgress.show()
        } else {
            binding.layoutProgress.gone()
        }
    }
}