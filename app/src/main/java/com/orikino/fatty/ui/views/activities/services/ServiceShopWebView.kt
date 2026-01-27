package com.orikino.fatty.ui.views.activities.services

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.MimeTypeMap
import android.webkit.SslErrorHandler
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ActivityServiceShopWebviewBinding
import com.orikino.fatty.domain.viewstates.ServiceViewState
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.ui.views.fragments.HomeFragment
import com.orikino.fatty.utils.ConfirmDialog
import com.orikino.fatty.utils.CustomToast
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.fixCutoutOfEdgeToEdge
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class ServiceShopWebView : AppCompatActivity() {

    private lateinit var binding: ActivityServiceShopWebviewBinding

    private val viewModel: ServicesViewModel by viewModels()

    private var isLoaded: Boolean = false

    private var storeId: Int = 0
    private var storeName: String = ""

    // FIXED: Add duplicate prevention flag
    private var isDownloadInProgress = false

    // Download handling
    private var downloadUrl: String? = null
    private var downloadFilename: String? = null
    private var downloadMimetype: String? = null

    private val PAYMENT_SUCCESS_KEYWORDS = listOf(
        "wavemoney",
        "kbzpay", "wavepay", "ayapay", "mpu", "jcb", "master", "visa", "unionpay", "uabpay", "mmqr"
    )

    // Permission launcher for download
    private val downloadPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startDownload()
        } else {
            CustomToast(
                this,
                "Storage permission required to download files",
                true
            ).createCustomToast()
        }
    }

    /**
     * Check storage permission
     */
    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ doesn't need storage permission for downloads
            true
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }


    companion object {
        const val STORE_ID = "STORE_ID"
        const val STORE_NAME = "STORE_NAME"

        fun getIntent(context: Context, storeId: Int, storeName: String): Intent {
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
        binding.ivEnd.setOnClickListener {
            exitConfirmDialog()
        }

        if (!isLoaded) {
            PreferenceUtils.readUserVO().customer_id?.let {
                viewModel.fetchShopWebLink(storeId, it)
            }
        }

        listenToViewModel()
    }

    private fun exitConfirmDialog() {
        ConfirmDialog.Builder(
            this,
            getString(R.string.txt_are_you_sure_you_want_to_exit),
            getString(R.string.txt_exit_description),
            getString(R.string.txt_exit),
            callback = {
                finish()
            })
            .show(
                supportFragmentManager,
                ServiceShopWebView::class.simpleName
            )
    }

    private fun listenToViewModel() {
        viewModel.viewState.observe(this) {
            render(it)
        }
    }

    override fun onDestroy() {
        fullyDestroyWebView()
        super.onDestroy()
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
            binding.webView.clearHistory()

            // Clear cookies
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies { success ->
                Log.d("WebViewFragment", "Cookies cleared: $success")
            }
            cookieManager.flush()

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

    private fun fullyDestroyWebView() {
        val webView = binding.webView

        webView.stopLoading()
        webView.loadUrl("about:blank")

        webView.clearHistory()
        webView.clearCache(true)
        webView.clearFormData()

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        webView.removeAllViews()
        webView.destroy()

        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
        WebStorage.getInstance().deleteAllData()
    }


    private fun render(state: ServiceViewState) {
        when (state) {
            is ServiceViewState.OnLoadingShopWebLink -> {
                onLoadingShopWebLink()
            }

            is ServiceViewState.OnSuccessShopWebLink -> {
                onSuccessShopWebLink(state)
            }

            is ServiceViewState.OnFailShopWebLink -> {
                onFailShopWebLink(state)
            }

            else -> {}
        }
    }

    private fun onLoadingShopWebLink() {
        loadingView(true)
    }

    private fun onSuccessShopWebLink(state: ServiceViewState.OnSuccessShopWebLink) {
        Log.d("WebViewFragment", "status: ${state.data.success}")
        if (state.data.success) {
            loadWebUrl(state.data.data?.web_view_link ?: "")
        } else {
            showInvalidDialog(state.data.message)
        }
    }

    private fun showInvalidDialog(desc: String) {
        val builder = ConfirmDialog.Builder(
            this,
            getString(R.string.txt_are_you_sure_you_want_to_exit),
            desc,
            getString(R.string.txt_exit),
            callback = {
                finish()
            },
            dismissCallback = {
                finish()
            }
        )
        builder.hideCancelBtn()
        builder.show(
            supportFragmentManager,
            ServiceShopWebView::class.simpleName
        )
    }

    private fun onFailShopWebLink(state: ServiceViewState.OnFailShopWebLink) {
        loadingView(false)
        when (state.message) {
            "Server Error" -> {
                //binding.layoutNetworkError.root.show()
            }

            "Another Login" -> {
                WarningDialog.Builder(
                    this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finishAffinity()
                        val intent = Intent(this, SplashActivity::class.java)
                        startActivity(intent)
                        //requireContext().startActivity<SplashActivity>()
                    }).show(supportFragmentManager, HomeFragment::class.simpleName)
            }

            "DENIED" -> WarningDialog.Builder(
                this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                }).show(supportFragmentManager, ServiceDetailActivity::class.simpleName)

            else -> {
                showSnackBar(state.message)
            }
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
                userAgentString = "$userAgentString MyAppWebView"
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
            // Add JavaScript interface for blob downloads
            addJavascriptInterface(object : Any() {
                @JavascriptInterface
                fun getBase64FromBlobData(base64Data: String, filename: String, mimeType: String) {
                    Log.d(
                        "WebViewFragment",
                        "Received blob data - filename: $filename, mime: $mimeType"
                    )
                    runOnUiThread {
                        handleBase64Download(base64Data, filename, mimeType)
                    }
                }
            }, "AndroidDownloader")

            // Setup download listener
            setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                handleDownload(url, userAgent, contentDisposition, mimetype, contentLength)
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url.toString()
                    Log.d("WebViewFragment", "shouldOverrideUrlLoading: $url")
                    // Check if URL is a downloadable file
                    if (isDownloadableUrl(url) && !url.startsWith("blob:")) {
                        handleDownload(url, "", "", getMimeTypeFromUrl(url), 0)
                        return true
                    }

                    if (url.contains("refer?method=pwaapp")){
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        startActivity(intent)
                        return true
                    }

//                    if (url.contains("/logout") || url.contains("/login")){
//                        PreferenceUtils.clearCache()
//                        finishAffinity()
//                        //startActivity<SplashActivity>()
//                        val intent = Intent(this@ServiceShopWebView, LoginActivity::class.java)
//                        startActivity(intent)
//                    }

                    if (url.startsWith("intent://")) {
                        try {
                            val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)

                            val packageManager = view?.context?.packageManager
                            if (intent.resolveActivity(packageManager!!) != null) {
                                // KBZPay installed → open app
                                view.context.startActivity(intent)
                            } else {
                                // 2️⃣ App not installed → fallback
                                val fallbackUrl =
                                    intent.getStringExtra("browser_fallback_url")

                                if (!fallbackUrl.isNullOrEmpty()) {
                                    view.loadUrl(fallbackUrl)
                                } else {
                                    // Optional: redirect to Play Store
                                    val marketIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=${intent.`package`}")
                                    )
                                    view.context.startActivity(marketIntent)
                                }
                            }
                            return true
                        } catch (e: Exception) {
                            Log.e("WebViewFragment", "Intent parse error", e)
                            return true
                        }
                    }

                    if (url.contains("accounts.google.com") ||
                        url.contains("oauth.google.com") ||
                        url.contains("facebook.com/login") ||
                        url.contains("twitter.com/oauth") ||
                        url.contains("maps.app.goo.gl")
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
                    injectBlobDownloadScript()

                    if (url?.contains("/login") == true) {
                        Log.e("WebViewFragment", "Ended on login page - token may be invalid")
                    }


                    if (isPaymentSuccessUrl(url)) {
                        // Clear payment pages from back stack
                        view?.clearHistory()
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

//                override fun onCreateWindow(
//                    view: WebView?,
//                    isDialog: Boolean,
//                    isUserGesture: Boolean,
//                    resultMsg: Message?
//                ): Boolean {
//                    // Load new window in same WebView
//                    val url = view?.hitTestResult?.extra ?: return false
//
//                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
//                    return false
//                }

                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    consoleMessage?.let {
                        Log.d(
                            "WebViewConsole",
                            "${it.message()} -- Line ${it.lineNumber()} of ${it.sourceId()}"
                        )
                    }
                    return true
                }
            }
        }
    }

    private fun isPaymentSuccessUrl(url: String?): Boolean {
        return PAYMENT_SUCCESS_KEYWORDS.any { url?.contains(it, true) == true }
    }

    /**
     * Check if URL is a downloadable file based on extension
     */
    private fun isDownloadableUrl(url: String): Boolean {
        val downloadExtensions = listOf(
            ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx",
            ".zip", ".rar", ".7z", ".apk", ".mp3", ".mp4", ".avi",
            ".mkv", ".mov", ".jpg", ".jpeg", ".png", ".gif", ".webp"
        )
        return downloadExtensions.any { url.lowercase().contains(it) }
    }

    /**
     * Get MIME type from URL
     */
    private fun getMimeTypeFromUrl(url: String): String {
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        return if (extension.isNotEmpty()) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
        } else {
            "*/*"
        }
    }

    private fun loadWebUrl(webUrl: String) {
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

    /**
     * FIXED: Inject JavaScript to intercept blob downloads ONLY when user clicks download links
     */
    private fun injectBlobDownloadScript() {
        val script = """
            (function() {
                // Only intercept EXPLICIT blob downloads (clicks on download links)
                document.addEventListener('click', function(e) {
                    let target = e.target;
                    
                    // Find the closest anchor tag
                    while (target && target.tagName !== 'A') {
                        target = target.parentElement;
                    }
                    
                    // Only process if it's a blob URL download link
                    if (target && target.href && target.href.startsWith('blob:') && target.download) {
                        e.preventDefault();
                        e.stopPropagation();
                        
                        console.log('Blob download intercepted:', target.href);
                        
                        fetch(target.href)
                            .then(response => response.blob())
                            .then(blob => {
                                const reader = new FileReader();
                                reader.onloadend = function() {
                                    const base64data = reader.result.split(',')[1];
                                    let filename = target.download || 'download_' + Date.now();
                                    
                                    // Ensure extension
                                    if (!filename.includes('.')) {
                                        const ext = getExtension(blob.type);
                                        filename += '.' + ext;
                                    }
                                    
                                    AndroidDownloader.getBase64FromBlobData(
                                        base64data, 
                                        filename, 
                                        blob.type
                                    );
                                };
                                reader.readAsDataURL(blob);
                            })
                            .catch(err => console.error('Download error:', err));
                    }
                }, true);
                
                function getExtension(mimeType) {
                    const extensions = {
                        'image/jpeg': 'jpg',
                        'image/jpg': 'jpg',
                        'image/png': 'png',
                        'image/gif': 'gif',
                        'image/webp': 'webp',
                        'video/mp4': 'mp4',
                        'video/webm': 'webm',
                        'application/pdf': 'pdf',
                        'application/zip': 'zip',
                        'text/plain': 'txt',
                        'text/csv': 'csv'
                    };
                    return extensions[mimeType] || 'bin';
                }
            })();
        """.trimIndent()

        binding.webView.evaluateJavascript(script, null)
    }
// ==================== DOWNLOAD HANDLING ====================

    /**
     * FIXED: Handle base64 encoded blob downloads with duplicate prevention
     */
    private fun handleBase64Download(base64Data: String, filename: String, mimeType: String) {
        // PREVENT DUPLICATES
        if (isDownloadInProgress) {
            Log.d("WebViewFragment", "Download already in progress, ignoring duplicate")
            return
        }

        isDownloadInProgress = true

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)

                withContext(Dispatchers.Main) {
                    if (checkStoragePermission()) {
                        saveBase64File(decodedBytes, filename, mimeType)
                    } else {
                        downloadUrl = base64Data
                        downloadFilename = filename
                        downloadMimetype = mimeType
                        requestStoragePermission()
                    }
                }
            } catch (e: Exception) {
                Log.e("WebViewFragment", "Error handling base64 download", e)
                withContext(Dispatchers.Main) {
                    CustomToast(
                        this@ServiceShopWebView,
                        "Download failed: ${e.message}",
                        true
                    ).createCustomToast()
                }
            } finally {
                // Reset flag after a short delay
                withContext(Dispatchers.Main) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        isDownloadInProgress = false
                    }, 2000)
                }
            }
        }
    }

    /**
     * Request storage permission
     */
    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // No permission needed, start download
            startDownload()
        } else {
            downloadPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    /**
     * Start the actual download using DownloadManager
     */
    private fun startDownload() {
        val url = downloadUrl ?: return

        // If it's base64 data (blob), handle differently
        if (!url.startsWith("http")) {
            Log.d("WebViewFragment", "Non-HTTP URL, skipping DownloadManager")
            return
        }

        val filename = downloadFilename ?: "download"

        try {
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setTitle(filename)
                setDescription("Downloading $filename")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)

                // Add headers from cookies
                val cookies = CookieManager.getInstance().getCookie(url)
                addRequestHeader("Cookie", cookies)
                addRequestHeader("User-Agent", binding.webView.settings.userAgentString)

                // Allow scanning by MediaScanner
                allowScanningByMediaScanner()

                // Set MIME type if available
                downloadMimetype?.let { setMimeType(it) }
            }

            val downloadManager =
                this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)

            Log.d("WebViewFragment", "Download started with ID: $downloadId")

            Toast.makeText(
                this,
                "Download started: $filename",
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: Exception) {
            Log.e("WebViewFragment", "Download failed", e)
            CustomToast(
                this,
                "Download failed: ${e.message}",
                true
            ).createCustomToast()
        } finally {
            // Clear download info
            downloadUrl = null
            downloadFilename = null
            downloadMimetype = null
        }
    }

    /**
     * FIXED: Save base64 file to storage with unique filenames
     */
    private fun saveBase64File(data: ByteArray, filename: String, mimeType: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Generate unique filename to prevent overwrites
                val uniqueFilename = generateUniqueFilename(filename)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveFileUsingMediaStore(data, uniqueFilename, mimeType)
                } else {
                    saveFileLegacy(data, uniqueFilename, mimeType)
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ServiceShopWebView,
                        "Downloaded: $uniqueFilename",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("WebViewFragment", "Error saving file", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ServiceShopWebView,
                        "Failed to save file: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * NEW: Generate unique filename with timestamp
     */
    private fun generateUniqueFilename(filename: String): String {
        val nameWithoutExt = filename.substringBeforeLast(".", filename)
        val extension = if (filename.contains(".")) {
            filename.substringAfterLast(".")
        } else {
            ""
        }

        val timestamp = System.currentTimeMillis()

        return if (extension.isNotEmpty()) {
            "${nameWithoutExt}_${timestamp}.$extension"
        } else {
            "${nameWithoutExt}_${timestamp}"
        }
    }

    /**
     * Save file using MediaStore API (Android 10+)
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    @Throws(IOException::class)
    private fun saveFileUsingMediaStore(
        data: ByteArray,
        filename: String,
        mimeType: String
    ): Uri {

        val resolver = this.contentResolver

        // 1️⃣ Choose correct collection & directory
        val (collection, relativePath) = when {
            mimeType.startsWith("image/") -> {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) to
                        Environment.DIRECTORY_PICTURES
            }

            mimeType.startsWith("video/") -> {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) to
                        Environment.DIRECTORY_MOVIES
            }

            mimeType.startsWith("audio/") -> {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) to
                        Environment.DIRECTORY_MUSIC
            }

            else -> {
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) to
                        Environment.DIRECTORY_DOWNLOADS
            }
        }

        // 2️⃣ Prepare metadata
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        // 3️⃣ Insert into MediaStore
        val uri = resolver.insert(collection, contentValues)
            ?: throw IOException("Failed to create MediaStore entry")

        try {
            // 4️⃣ Write file
            resolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(data)
                outputStream.flush()
            } ?: throw IOException("Failed to open output stream")

            // 5️⃣ Mark file as complete
            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            Log.d("WebViewFragment", "File saved successfully: $uri")
            return uri

        } catch (e: Exception) {
            // Clean up partially written file
            resolver.delete(uri, null, null)
            throw e
        }
    }


    /**
     * Save file directly to Downloads folder (Android 9 and below)
     */
    private fun saveFileLegacy(data: ByteArray, filename: String, mimeType: String) {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }

        val file = File(downloadsDir, filename)
        file.outputStream().use { it.write(data) }

        // Notify media scanner
        MediaScannerConnection.scanFile(
            this,
            arrayOf(file.absolutePath),
            arrayOf(mimeType)
        ) { path, uri ->
            Log.d("WebViewFragment", "File scanned: $path")
        }

        Log.d("WebViewFragment", "File saved: ${file.absolutePath}")
    }

    /**
     * FIXED: Handle download request with duplicate prevention
     */
    private fun handleDownload(
        url: String,
        userAgent: String,
        contentDisposition: String,
        mimetype: String,
        contentLength: Long
    ) {
        Log.d("WebViewFragment", "Download requested - URL: $url")
        Log.d("WebViewFragment", "MIME type: $mimetype, Size: $contentLength")

        // IGNORE BLOB URLs - let JavaScript handler deal with them
        if (url.startsWith("blob:")) {
            Log.d("WebViewFragment", "Blob URL detected, JavaScript handler will process it")
            return
        }

        // PREVENT DUPLICATES for regular downloads
        if (isDownloadInProgress) {
            Log.d("WebViewFragment", "Download already in progress, ignoring duplicate")
            return
        }

        isDownloadInProgress = true

        // Reset flag after delay
        Handler(Looper.getMainLooper()).postDelayed({
            isDownloadInProgress = false
        }, 2000)

        // Store download info
        downloadUrl = url
        downloadMimetype = mimetype
        downloadFilename = getFilenameFromUrl(url, contentDisposition, mimetype)

        // Check permission and start download
        if (checkStoragePermission()) {
            showDownloadDialog()
        } else {
            requestStoragePermission()
        }
    }

    /**
     * Show download confirmation dialog
     */
    private fun showDownloadDialog() {
        val fileType = when {
            downloadMimetype?.contains("image") == true -> "Image"
            downloadMimetype?.contains("video") == true -> "Video"
            downloadMimetype?.contains("audio") == true -> "Audio"
            downloadMimetype?.contains("pdf") == true -> "PDF"
            else -> "File"
        }

        AlertDialog.Builder(this)
            .setTitle("Download $fileType")
            .setMessage("Do you want to download:\n$downloadFilename?")
            .setPositiveButton("Download") { _, _ ->
                startDownload()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * FIXED: Extract filename with unique timestamp to prevent duplicates
     */
    private fun getFilenameFromUrl(
        url: String,
        contentDisposition: String,
        mimetype: String
    ): String {
        var filename = URLUtil.guessFileName(url, contentDisposition, mimetype)

        if (filename.startsWith("downloadfile") || filename == "Unknown") {
            val uri = Uri.parse(url)
            val pathSegments = uri.pathSegments
            if (pathSegments.isNotEmpty()) {
                filename = pathSegments.last()
            }
        }

        // Ensure proper extension
        if (!filename.contains(".")) {
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimetype)
            if (!extension.isNullOrEmpty()) {
                filename = "$filename.$extension"
            }
        }

        // Make filename unique with timestamp
        return generateUniqueFilename(filename)
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        } else {
            super.attachBaseContext(newBase)
        }
    }

}