package com.orikino.fatty.utils.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Typeface
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.orikino.fatty.R
import com.orikino.fatty.domain.model.CategoryVO
import com.orikino.fatty.domain.model.CityVO
import com.orikino.fatty.domain.model.CurrencyVO
import com.orikino.fatty.domain.model.FoodMenuByRestaurantVO
import com.orikino.fatty.domain.model.FoodSubItemVO
import com.orikino.fatty.domain.model.FoodVO
import com.orikino.fatty.domain.model.MenuVO
import com.orikino.fatty.domain.model.NearByRestaurantVO
import com.orikino.fatty.domain.model.NewRecommendRestaurantVO
import com.orikino.fatty.domain.model.OptionVO
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.domain.model.SearchFoodsVO
import com.orikino.fatty.domain.model.SearchRestaurantVO
import com.orikino.fatty.domain.model.TopRelatedCategoryVO
import com.orikino.fatty.domain.responses.TempRestDetailResponse
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.SmartScrollListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val rcCamera = 1
const val rcGallery = 2
const val cameraImagePermissionRequest = 1
const val VALUE_MAP_ZOOM = 15f
const val HOME_VALUE_MAP_ZOOM = 18f
const val VALUE_MAP_TILE_COUNT = 20f

fun isNetworkAvailable(context: Context): Boolean {
    var result = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }

            }
        }
    }
    return result
}

@SuppressLint("ObsoleteSdkInt")
fun Context.isConnected(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val activeNetwork = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(activeNetwork)
        capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(
            NetworkCapabilities.TRANSPORT_WIFI
        ) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
    } else {
        val networkInfo = cm.activeNetworkInfo
        networkInfo?.isConnected ?: false
    }
}

fun Activity.hideSoftKeyboard() {
    currentFocus?.let {
        val inputMethodManager =
            ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

//|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
fun Context.isLocationEnabled(): Boolean {
    val locationManager: LocationManager =
        this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

}

fun <T> T?.nullSafeToString() = this?.toString() ?: "NULL"



fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.unShow() {
    this.visibility = View.INVISIBLE
}


fun View.gone() {
    this.visibility = View.GONE
}


fun deactivatedBtn(btn : AppCompatButton) {
    btn.alpha = 0.2f
    btn.isClickable = false
}

fun activatedBtn(btn : AppCompatButton) {
    btn.alpha = 1f
    btn.isClickable = true
}

fun AppCompatImageView.loadPhoto(photo : Any) {
    this.load(photo) {
        crossfade(true)
        placeholder(R.drawable.restaurant_default_img)
        //transformations(CircleCropTransformation())
    }
}

fun Context.correctLocale() {
    PreferenceUtils.readLanguage()?.let { forceUpdateLocale(it) }
}

fun setColorSpannable(text : String , color : Int , startIndex : Int , endIndex : Int) : SpannableString {
    val spannableString = SpannableString(text)
    spannableString.setSpan(
        ForegroundColorSpan(color) ,
        startIndex ,
        endIndex ,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}

fun setBoldSpannable(text : String , startIndex : Int , endIndex : Int) : SpannableString {
    val spannableString = SpannableString(text)
    spannableString.setSpan(
        StyleSpan(Typeface.BOLD) ,
        startIndex ,
        endIndex ,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}

fun Context.forceUpdateLocale(language: String, callback: () -> Unit = {}) {
    currentResource(language)
    callback.invoke()
}

fun Context.currentResource(language: String) {
    PreferenceUtils.writeLanguage(language)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
    } else {
        //updateResourcesLegacy(this, language)
        LocaleHelper().setLocale(this, language)
    }
}

private fun updateResources(context: Context, language: String): Context? {
    val locale = Locale(language)
    Locale.setDefault(locale)

    val configuration = context.resources.configuration
    configuration.setLocale(locale)
    configuration.setLayoutDirection(locale)

    return context.createConfigurationContext(configuration)
}

@Suppress("deprecation")
private fun updateResourcesLegacy(context: Context, language: String): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)

    val resources = context.resources

    val configuration = resources.configuration
    configuration.locale = locale
    configuration.setLayoutDirection(locale)

    resources.updateConfiguration(configuration, resources.displayMetrics)

    return context
}

private fun changeLanguageLegacy(context: Context, language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)

    // Update resources for the current context
    val currentResources = context.resources
    val currentConfig = Configuration(currentResources.configuration)
    currentConfig.setLocale(locale)
    currentResources.updateConfiguration(currentConfig, currentResources.displayMetrics)

    // Update resources for the application context
    val applicationContext = context.applicationContext
    if (applicationContext != null && applicationContext.resources != currentResources) {
        val applicationResources = applicationContext.resources
        val applicationConfig = Configuration(applicationResources.configuration)
        applicationConfig.setLocale(locale)
        applicationResources.updateConfiguration(applicationConfig, applicationResources.displayMetrics)
    }
}



fun Activity.phoneCall(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$phone")
    startActivity(intent)
}

fun Context.phoneCall(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$phone")
    this.startActivity(intent)
}

fun RecyclerView.applySmartScrollListener(listener: SmartScrollListener.OnSmartScrollListener) {
    this.addOnScrollListener(SmartScrollListener(listener))
}

fun AppCompatImageView.load(source: Any) {
    //val requestOptions: RequestOptions by KoinJavaComponent.inject(RequestOptions::class.java)
    /*Glide.with(this)
        .load(source)
        //.placeholder(R.drawable.placeholder)
        .into(this)*/
}

fun Activity.getVerticalLayoutManager() = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

fun Fragment.getVerticalLayoutManager() =
    LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

fun ViewGroup.getInflatedLayout(@LayoutRes layout: Int): View {
    return LayoutInflater.from(context).inflate(layout, this, false)
}

fun String.toRequestBody() = this.toRequestBody(MultipartBody.FORM)

fun AppCompatActivity.showSnackBar(message: String) {
    Snackbar.make(window.decorView, message, Snackbar.LENGTH_SHORT).show()
}

fun Activity.getImageMultipleFile(name: String, file: File): MultipartBody.Part {
    val requestFile: RequestBody =
        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
    return MultipartBody.Part.createFormData(name, file.name, requestFile)

}

fun Fragment.showSnackBar(message: String) {
    Snackbar.make(requireActivity().window.decorView, message, Snackbar.LENGTH_SHORT).show()
}

fun Int.toThousandSeparator() = String.format(Locale.US, "%,.2f", this)
fun Double.toThousandSeparator() = String.format(Locale.US, "%,.2f", this)

/*fun AppCompatImageView.loadImageGlide(source: String) {
    Glide.with(context)
        .asBitmap()
        .load(source)
        .placeholder(R.drawable.placeholder)
        .apply(RequestOptions.formatOf(DecodeFormat.PREFER_RGB_565).override(350, 350))
        .diskCacheStrategy(
            DiskCacheStrategy.ALL
        ).into(this)
//    Glide.with(context).load(source).placeholder(R.drawable.placeholder).diskCacheStrategy(
//        DiskCacheStrategy.ALL
//    ).into(this)
}*/

/*fun AppCompatImageView.loadImage(source: String) {
    Glide.with(context).load(source).placeholder(R.drawable.placeholder).diskCacheStrategy(
        DiskCacheStrategy.ALL
    ).into(this)
}*/

/*fun AppCompatImageView.loadParcelImage(source: String) {

    Picasso.get().load(source).placeholder(R.drawable.placeholder).into(this)
//    Glide.with(context).load(source).placeholder(R.drawable.placeholder).diskCacheStrategy(
//        DiskCacheStrategy.ALL
//    ).into(this)

    *//*Glide.with(context)
        .load(source)
        .placeholder(R.drawable.placeholder)
        .into(this)*//*

}*/

/*
fun AppCompatImageView.loadSlide(source: String) {

//    Glide.with(context).load(source).placeholder(R.drawable.placeholder).diskCacheStrategy(
//        DiskCacheStrategy.ALL
//    ).into(this)

    Glide.with(context)
        .asBitmap()
        .load(source)
        .placeholder(R.drawable.placeholder)
        .apply(RequestOptions.formatOf(DecodeFormat.PREFER_RGB_565).override(150, 150))
        .diskCacheStrategy(
            DiskCacheStrategy.ALL
        ).into(this)
    //Picasso.get().load(source).placeholder(R.drawable.placeholder).into(this)
}

fun CircleImageView.loadSlide(source: String) {
    Glide.with(context).load(source).placeholder(R.drawable.placeholder).diskCacheStrategy(
        DiskCacheStrategy.ALL
    ).into(this)
    //Picasso.get().load(source).into(this)
}

fun CircleImageView.loadProfile(source: String) {
    Glide.with(context).load(source).placeholder(R.drawable.placeholder).diskCacheStrategy(
        DiskCacheStrategy.ALL
    ).into(this)
    //Picasso.get().load(source).placeholder(R.drawable.ic_circle_account).into(this)
}


fun RecyclerView.smoothScrollRvToPosition(
    position: Int,
    snapMode: Int = LinearSmoothScroller.SNAP_TO_START
) {
    val smoothScroller = object : LinearSmoothScroller(this.context) {
        override fun getVerticalSnapPreference(): Int {
            return snapMode
        }
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
}
*/

@SuppressLint("ResourceAsColor")
fun Context.createChip(string: String): Chip {

    val states = arrayOf(
        intArrayOf(-android.R.attr.state_checked),
        intArrayOf(android.R.attr.state_checked)
    )
    val colors = intArrayOf(
        ContextCompat.getColor(this, R.color.surfaceUnread),
        ContextCompat.getColor(this, R.color.fattyPrimary)
    )

    val colorsStateList = ColorStateList(states, colors)

    val chip = Chip(this)
    chip.text = string
    chip.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
    chip.textSize = 12f
    chip.isCheckable = true
    chip.setTextColor(ColorStateList.valueOf("#2E2E2E".toColorInt()))
    chip.chipStrokeWidth = 1f
    chip.chipStrokeColor = ColorStateList.valueOf("#E1E1E1".toColorInt())
    chip.chipBackgroundColor = colorsStateList
    /*chip.setOnCheckedChangeListener { compoundButton, b ->
        if (b) {
            chip.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
            chip.chipBackgroundColor = colorsStateList
            //chip.setChipBackgroundColorResource(R.color.fatty)
        } else {
            chip.setTextColor(ColorStateList.valueOf(Color.parseColor("#F87B53")))
            chip.chipStrokeWidth = 1f
            chip.chipStrokeColor = ColorStateList.valueOf(Color.parseColor("#F87B53"))
            chip.chipBackgroundColor = colorsStateList
            //chip.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
        }
    }
    */

    chip.isCheckedIconVisible = false

    return chip
}


fun cameraIntent(): Intent {
    return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
}

fun pickImageIntent(): Intent {
    return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
}

fun Context.showImagePickerDialog(callback: (Intent, Int) -> Unit = { _, _ -> }) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(resources.getString(R.string.select_profile_photo))
    builder.setItems(
        arrayOf(
            resources.getString(R.string.take_photo),
            resources.getString(R.string.select_from_gallery),

            )
    ) { _: DialogInterface?, which: Int ->
        when (which) {
            0 -> callback(cameraIntent(), rcCamera)
            1 -> callback(pickImageIntent(), rcGallery)
            else -> {
            }
        }
    }
    val dialog = builder.create()
    dialog.setCancelable(true)
    dialog.show()
}

val cameraImagePermissions = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.CAMERA
)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
val cameraImagePermissionsAndroid13 = arrayOf(
    Manifest.permission.READ_MEDIA_IMAGES,
    Manifest.permission.READ_MEDIA_VIDEO,
    Manifest.permission.CAMERA
)

fun Context.hasPermission(permissions: Array<String>): Boolean {
    return permissions.toList()
        .none {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
}

fun AppCompatActivity.makeRequest(request: Int, permissions: Array<String>) {
    ActivityCompat.requestPermissions(
        this,
        permissions,
        request
    )
}


fun showGPSNotEnabledDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Enable GPS")
        .setMessage("We need to know your location for delivery food to you")
        .setCancelable(false)
        .setPositiveButton("Enable Now") { _, _ ->
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        .show()
}

fun String.dateFromServerDateStringForTopPlayer(): Date {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.US)
    return parser.parse(this)!!
}

fun String.dateFormatOne() : Date {
    val parser = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    return parser.parse(this)!!
}

fun Date?.toDateTimeStandardIn12Hours(): String {
    val pattern = "MMMM dd YYYY"
    return dateAsString(this, pattern)
}

fun Date?.toDateTimeStandard(): String {
    val pattern = "dd-MM-YYYY"
    return dateAsString(this, pattern)
}

private fun dateAsString(date: Date?, pattern: String): String {
    var simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    if (date != null)
        return simpleDateFormat.format(date)
    else
        throw NullPointerException("Date can't be null")
}

//fun OrderStatusVO.toDefaultStatusName() : String? {
//    return when (PreferenceUtils.readLanguage()) {
//        "en" -> {
//            this.order_status_name ?: "Order Success, waiting for accept by restaurant"
//        }
//        "zh" -> {
//            this.order_status_name_ch
//        }
//        else -> {
//            this.order_status_name_mm
//        }
//    }
//}



fun CategoryVO?.toDefaultCategoryName(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this?.restaurant_category_name_en ?: this?.restaurant_category_name_mm
        }
        "zh" -> {
            this?.restaurant_category_name_ch ?: this?.restaurant_category_name_mm
        }
        else -> {
            this?.restaurant_category_name_mm
        }
    }
}

fun FoodVO.toDefaultFoodName(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.food_name_en ?: this.food_name_mm
        }
        "zh" -> {
            this.food_name_ch ?: this.food_name_mm
        }
        else -> {
            this.food_name_mm
        }
    }
}

fun NearByRestaurantVO.toDefaultRestaurantName() : String? {
    return when(PreferenceUtils.readLanguage()) {
        "en" -> {
            this.restaurant_name ?: this.restaurant_name_en ?: "Hello"
        }
        "zh" -> {
            this.restaurant_name ?: this.restaurant_name_ch ?: "Hello"
        }
        else -> {
            this.restaurant_name ?: this.restaurant_name_mm ?: "Hello"
        }
    }
}

fun NearByRestaurantVO.toDefaultAddress() : String? {
    return when(PreferenceUtils.readLanguage()) {
        "en" -> {
            this.restaurant_address ?: this.restaurant_address_en ?: "Lashio"
        }
        "zh" -> {
            this.restaurant_address ?: this.restaurant_address_ch ?: "Lashio"
        }
        else -> {
            this.restaurant_address ?: this.restaurant_address_mm ?: "Lashio"
        }
    }
}


fun TopRelatedCategoryVO.toDefaultRestaurantName() : String {
    return when(PreferenceUtils.readLanguage()) {
        "en" -> {
            this.restaurant_name
        }
        "zh" -> {
            this.restaurant_name
        }
        else -> {
            this.restaurant_name
        }
    }
}

fun NearByRestaurantVO.toDefaultRestaurantCategoryName() : String? {
    return when(PreferenceUtils.readLanguage()) {
        "en" -> {
            this.restaurant_category_name ?: this.restaurant_name ?: "Hello"
        }
        "zh" -> {
            this.restaurant_category_name ?: this.restaurant_name ?: "Hello"
        }
        else -> {
            this.restaurant_category_name ?: this.restaurant_name ?: "Hello"
        }
    }
}

fun NewRecommendRestaurantVO.toDefaultRestaurantName() : String? {
    return when(PreferenceUtils.readLanguage()) {
        "en" -> {
            this.restaurant_name ?: this.restaurant_name ?: "Hello"
        }
        "zh" -> {
            this.restaurant_name ?: this.restaurant_name ?: "Hello"
        }
        else -> {
            this.restaurant_name ?: this.restaurant_name ?: "Hello"
        }
    }
}

fun RecommendRestaurantVO.toDefaultCategoryName(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.category.restaurant_category_name_en ?: this.category.restaurant_category_name_mm
        }
        "zh" -> {
            this.category.restaurant_category_name_ch ?: this.category.restaurant_category_name_mm
        }
        else -> {
            this.category.restaurant_category_name_mm
        }
    }
}

fun RecommendRestaurantVO?.toDefaultRestaurantCategoryName(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this?.restaurant_category_name_en ?: this?.category?.restaurant_category_name_mm
        }
        "zh" -> {
            this?.restaurant_category_name_ch ?: this?.category?.restaurant_category_name_mm
        }
        else -> {
            this?.restaurant_category_name_mm
        }
    }
}

fun EditText.onSearch(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            callback.invoke()
            return@setOnEditorActionListener false
        }
        false
    }
}

fun getScreenWidth(context: Context, percentage: Double): Int {
    val displayMetrics = Resources.getSystem().displayMetrics
    return (displayMetrics.widthPixels * percentage).toInt()
}


fun RecommendRestaurantVO.toDefaultRestaurantName(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.restaurant_name_en ?: this.restaurant_name_mm ?: "Hello"
        }
        "zh" -> {
            this.restaurant_name_ch ?: this.restaurant_name_mm ?: "Hello"
        }
        else -> {
            this.restaurant_name_mm ?: "Hello"
        }
    }
}

fun RecommendRestaurantVO.toDefaultRestaurantAddress(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.restaurant_address_en ?: this.restaurant_address_mm
        }
        "zh" -> {
            this.restaurant_address_ch ?: this.restaurant_address_mm
        }
        else -> {
            this.restaurant_address_mm
        }

    }
}



fun FoodSubItemVO.toDefaultSectionName(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.section_name_en ?: this.section_name_mm
        }
        "zh" -> {
            this.section_name_ch ?: this.section_name_mm
        }
        else -> {
            this.section_name_mm
        }
    }
}

fun OptionVO.toDefaultOptionName(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.item_name_en ?: this.item_name_mm
        }
        "zh" -> {
            this.item_name_ch ?: this.item_name_mm
        }
        else -> {
            this.item_name_mm
        }
    }
}

fun SearchFoodsVO.toDefaultFoodName(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.food_name_en ?: this.food_name_mm
        }
        "zh" -> {
            this.food_name_ch ?: this.food_name_mm
        }
        else -> {
            this.food_name_mm
        }
    }
}

fun SearchRestaurantVO.toDefaultRestaurantName(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.restaurant_name_en ?: this.restaurant_name_mm
        }
        "zh" -> {
            this.restaurant_name_ch ?: this.restaurant_name_mm
        }
        else -> {
            this.restaurant_name_mm
        }
    }
}

fun SearchRestaurantVO.toDefaultCategoryName(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.restaurant_category_name_en ?: this.restaurant_category_name_mm
        }
        "zh" -> {
            this.restaurant_category_name_ch ?: this.restaurant_category_name_mm
        }
        else -> {
            this.restaurant_category_name_mm
        }
    }
}

fun SearchRestaurantVO.toDefaultRestaurantAddress(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.restaurant_address_en ?: this.restaurant_address_mm
        }
        "zh" -> {
            this.restaurant_address_ch ?: this.restaurant_address_mm
        }
        else -> {
            this.restaurant_address_mm
        }
    }
}


fun FoodMenuByRestaurantVO.toDefaultRestaurantName(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.restaurant_name_en ?: this.restaurant_name_mm
        }
        "zh" -> {
            this.restaurant_name_ch ?: this.restaurant_name_mm
        }
        else -> {
            this.restaurant_name_mm
        }
    }
}

fun FoodMenuByRestaurantVO.toDefaultRestaurantAddress(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.restaurant_address_en ?: this.restaurant_name_mm
        }
        "zh" -> {
            this.restaurant_address_ch ?: this.restaurant_name_mm
        }
        else -> {
            this.restaurant_address_mm
        }
    }
}

fun TempRestDetailResponse.Data.toDefaultRestaurantName() : String {
    return  when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.restaurant_name_en ?: this.restaurant_address_mm
        }
        "zh" -> {
            this.restaurant_name_ch ?: this.restaurant_address_mm
        }
        else -> {
            this.restaurant_name_mm
        }
    }
}


fun TempRestDetailResponse.Data.Menu.toDefaultMenuName() : String {
    return  when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.food_menu_name_en ?: this.food_menu_name_en
        }
        "zh" -> {
            this.food_menu_name_ch ?: this.food_menu_name_ch
        }
        else -> {
            this.food_menu_name_mm
        }
    }
}

fun TempRestDetailResponse.Data.toDefaultRestaurantAddress() : String {
    return  when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.restaurant_address_en ?: this.restaurant_address_mm
        }
        "zh" -> {
            this.restaurant_address_ch ?: this.restaurant_address_mm
        }
        else -> {
            this.restaurant_address_mm
        }
    }
}

fun FoodMenuByRestaurantVO.toDefaultAddress(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.restaurant_address_en ?: this.restaurant_address_mm
        }
        "zh" -> {
            this.restaurant_address_ch ?: this.restaurant_address_mm
        }
        else -> {
            this.restaurant_address_mm
        }
    }
}

fun CurrencyVO.toDefaultImage() : String {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.image
        }
        "zh" -> {
            this.image
        }
        else -> {
            this.image
        }
    }
}

fun MenuVO.toDefaultMenuName(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.food_menu_name_en ?: this.food_menu_name_mm
        }
        "zh" -> {
            this.food_menu_name_ch ?: this.food_menu_name_mm
        }
        else -> {
            this.food_menu_name_mm
        }
    }
}

fun Context.showDatePickerDialog(callback: (String) -> Unit) {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog =
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, _, monthOfYear, dayOfMonth ->
            c.set(Calendar.YEAR, year)
            c.set(Calendar.MONTH, monthOfYear)
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            //callback(SimpleDateFormat("dd-MM-yyyy", Locale.US).format(c.time))
            callback(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(c.time))
        }, year, month, day)

    datePickerDialog.show()

    datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
        .setTextColor(ContextCompat.getColor(this, R.color.fattyPrimary))
    datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
        .setTextColor(ContextCompat.getColor(this, R.color.fattyPrimary))

}

fun Fragment.rangeDatePickerDialog(date: (Pair<String, String>) -> Unit, uiDate: (String) -> Unit) {
    val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
    datePicker.show(this.requireActivity().supportFragmentManager, "DatePicker")


    // Setting up the event for when ok is clicked
    datePicker.addOnPositiveButtonClickListener {

        /*date(Pair(DateFormat.format("dd-MM-yyyy",Date(it.first)).toString(),DateFormat.format("dd-MM-yyyy",Date(it.second)).toString()))
        uiDate("${DateFormat.format("dd MMM yyyy",Date(it.first))} - ${DateFormat.format("dd MMM yyyy",Date(it.second))} ")*/
        date(
            Pair(
                SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date(it.first)).toString(),
                SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date(it.second)).toString()
            )
        )
        uiDate(
            "${
                SimpleDateFormat(
                    "dd-MMM-yyyy",
                    Locale.US
                ).format(Date(it.first))
            } - ${SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(Date(it.second))}"
        )

        // Toast.makeText(this.requireContext(), "${datePicker.headerText} is selected", Toast.LENGTH_LONG).show()
    }

    // Setting up the event for when cancelled is clicked
    datePicker.addOnNegativeButtonClickListener {
    }

    // Setting up the event for when back button is pressed
    datePicker.addOnCancelListener {

    }


}



fun CityVO.toDefaultCityName(): String? {
    return when (PreferenceUtils.readLanguage()) {
        "en" -> {
            this.city_name_en ?: this.city_name_mm
        }

        else -> {
            this.city_name_mm
        }
    }
}

/*fun UpAndDownVO.toDefaultAds() : String? {
    return when (Preference.readLanguage()) {
        "en" -> {
            this.image_en ?: this.image_mm
        }
        "zh" -> {
            this.image_ch ?: this.image_mm
        }
        else -> {
            this.image_mm
        }
    }
}*//*

fun CityVO.toDefaultCityName(): String? {
    return when (Preference.readLanguage()) {
        "en" -> {
            this.city_name_en ?: this.city_name_mm
        }

        else -> {
            this.city_name_mm
        }
    }
}

fun Activity.hideSoftKeyboard() {
    currentFocus?.let {
        val inputMethodManager =
            ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

@FlowPreview
@OptIn(ExperimentalCoroutinesApi::class)
fun Context.correctLocale() {
    forceUpdateLocale(Preference.readLanguage())

}



@FlowPreview
fun Context.forceUpdateLocale(language: String, callback: () -> Unit = {}) {
    currentResource(language)
    callback.invoke()

}

@FlowPreview
@OptIn(ExperimentalCoroutinesApi::class)
fun Context.currentResource(language: String): Resources {
    Preference.writeLanguage(language)
    val locale = Locale(language)
    Locale.setDefault(locale)
    //val configuration = Configuration(resources.configuration)
    val configuration = this.resources.configuration
    configuration.setLocale(locale)
    createConfigurationContext(configuration)
    resources.updateConfiguration(configuration, resources.displayMetrics)
    return resources
}

fun Context.phoneCall(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$phone")
    this.startActivity(intent)
}

fun Context.checkGpsStatus(): Boolean {
    var isEnable = false
    var locationManager: LocationManager =
        this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    isEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    return isEnable
}

fun Context.showDatePickerDialog(callback: (String) -> Unit) {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog =
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, _, monthOfYear, dayOfMonth ->
            c.set(Calendar.YEAR, year)
            c.set(Calendar.MONTH, monthOfYear)
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            callback(SimpleDateFormat("dd-MM-yyyy", Locale.US).format(c.time))
        }, year, month, day)

    datePickerDialog.show()

    datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
        .setTextColor(ContextCompat.getColor(this, R.color.fatty))
    datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
        .setTextColor(ContextCompat.getColor(this, R.color.fatty))

}

fun Double.toThousandSeparator() = String.format(Locale.US, "%,.2f", this)
//fun Double.toThousandSeparator() = String.format("%,.2f", this)

fun Fragment.rangeDatePickerDialog(date: (Pair<String, String>) -> Unit, uiDate: (String) -> Unit) {
    val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
    datePicker.show(this.requireActivity().supportFragmentManager, "DatePicker")


    // Setting up the event for when ok is clicked
    datePicker.addOnPositiveButtonClickListener {

        *//*date(Pair(DateFormat.format("dd-MM-yyyy",Date(it.first)).toString(),DateFormat.format("dd-MM-yyyy",Date(it.second)).toString()))
        uiDate("${DateFormat.format("dd MMM yyyy",Date(it.first))} - ${DateFormat.format("dd MMM yyyy",Date(it.second))} ")*//*
        date(
            Pair(
                SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date(it.first)).toString(),
                SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date(it.second)).toString()
            )
        )
        uiDate(
            "${
                SimpleDateFormat(
                    "dd-MMM-yyyy",
                    Locale.US
                ).format(Date(it.first))
            } - ${SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(Date(it.second))}"
        )

        // Toast.makeText(this.requireContext(), "${datePicker.headerText} is selected", Toast.LENGTH_LONG).show()
    }

    // Setting up the event for when cancelled is clicked
    datePicker.addOnNegativeButtonClickListener {
    }

    // Setting up the event for when back button is pressed
    datePicker.addOnCancelListener {

    }


}*/







