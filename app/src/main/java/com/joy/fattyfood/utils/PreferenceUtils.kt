package com.joy.fattyfood.utils

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.joy.fattyfood.domain.model.*
import io.paperdb.Paper

object PreferenceUtils {

    const val PRODUCTION_URL = "http://128.199.241.129/"
    const val DEVELOPMENT_URL = "http://188.166.226.161/"
    //const val DEVELOPMENT_URL = "http://165.22.98.246/"



    const val IMAGE_URL = "https://fatty-spaces.sgp1.cdn.digitaloceanspaces.com/testing"

    const val TERM_CONDITION = "fatty/main/admin/term&condition"

    private const val USER_INFO = "user-info"
    private const val FIRST_TIME = "first-time"
    private const val LANGUAGE = "language"
    private const val FOOD_ORDER = "food_order"
    private const val CHECK_CART = "check_cart"
    private const val RESTAURANT = "restaurant"
    private const val SENDER_RECEIVER = "sender-receiver"
    private const val PARCEL_INFO = "parcel-info"
    private const val REST_NOTE = "rest-note"
    private const val SELECTED_ADDRESS = "selected_address"
    private const val ISSelected = "is_selected"
    private const val MANAGE_ADDRESS = "manage_address"
    private const val DEVICE_TOKEN = "device-token"
    private const val FROM_CART = "from-cart"
    private const val SHOW_ALERT = "show-alert"
    private const val CURRENCY_TYPE = "currency-type"
    private const val HOME_CITY_ID = "home_city"
    private const val ZONE_ID = "zone-id"
    private const val CURRENCY_ID = "currency-id"
    private const val PARCEL_ZONE_ID = "parcel_zone_id"
    private const val CURRENCY_VO = "currency"
    private const val CHOOSE_PARCEL_ADDRESS = "choose_parcel_address"
    private const val ONBOARD_AD = "onboard_ad"
    private const val CUSTOMER_ORDER_ID = "customer_order_id"
    private const val SEARCH_RECENT = "search_recent"
    private const val CURR_ID = "currency-id"


    var needToShow = true
    var isHome = MutableLiveData(false)
    var wishListCount = MutableLiveData<Int>(0)
    //var nearbyRestaurant = MutableLiveData<MutableList<RecommendRestaurantVO>>()

    var topRatedRest = MutableLiveData<MutableList<RecommendRestaurantVO>>()
    var topCategories = MutableLiveData<MutableList<CategoryVO>>()
    var wishListRestaurant = MutableLiveData<Pair<Int, Boolean>>(Pair(0, false))
    var cartCount = MutableLiveData<Int>(0)
    var notiCount = MutableLiveData<Int>(0)
    var acceptOrder = MutableLiveData<Boolean>(false)
    var refreshOrderList = MutableLiveData<String>("")
    var isGpsEnable = MutableLiveData(true)
    var selectedFoodItemPrice = 0.0
    var isBackground = false
    //var ParcelAddressVO = MutableLiveData<ParcelAddressVO>()
    var searchFoodVO = MutableLiveData<MutableList<SearchFoodsVO>>()
    var orderId = MutableLiveData<String>("")
    var searchRecent = MutableLiveData<String>("")


    var FROM_HOME = 1

    fun writeUserVO(value: CustomerVO) {
        Paper.book().write(USER_INFO, value)
    }

    fun readUserVO(): CustomerVO = Paper.book().read(USER_INFO, CustomerVO())!!

    fun writeFirstTimeAd(value: Boolean) {
        Paper.book().write(ONBOARD_AD,value)
    }

    fun readFirstTimeAd() : Boolean? = Paper.book().read(ONBOARD_AD, true)

    fun writeFirstTime(value: Boolean) {
        Paper.book().write(FIRST_TIME, value)
    }

    fun readFirstTimeUserVO(): Boolean? = Paper.book().read(FIRST_TIME, true)

    fun writeLanguage(value: String) {
        Paper.book().write(LANGUAGE, value)
    }

    fun readLanguage(): String? = Paper.book().read(LANGUAGE, "en")

    fun writeFoodOrderList(value: MutableList<CreateFoodVO>) {
        Paper.book().write(FOOD_ORDER, value)
    }

    fun readFoodOrderList(): MutableList<CreateFoodVO>? =
        Paper.book().read(FOOD_ORDER, mutableListOf())

    fun writeAddToCart(value: Boolean) {
        Paper.book().write(CHECK_CART, value)
    }

    fun readAddToCart(): Boolean? = Paper.book().read(CHECK_CART, false)

    fun writeRestaurant(value: FoodMenuByRestaurantVO) {
        Paper.book().write(RESTAURANT, value)
    }

    fun readRestaurant(): FoodMenuByRestaurantVO? =
        Paper.book().read(RESTAURANT, FoodMenuByRestaurantVO())

    fun writeSenderReceiver(value: ParcelSenderReceiverVO) {
        Paper.book().write(SENDER_RECEIVER, value)
    }

    fun readSenderReceiver(): ParcelSenderReceiverVO? =
        Paper.book().read(SENDER_RECEIVER, ParcelSenderReceiverVO())

    fun writeParcelInfo(value: ParcelInfoVO) {
        Paper.book().write(PARCEL_INFO, value)
    }

    fun readParcelInfo(): ParcelInfoVO? = Paper.book().read(PARCEL_INFO, ParcelInfoVO())

    fun writeRestaurantNote(value: String) {
        Paper.book().write(REST_NOTE, value)
    }

    fun readRestaurantNote(): String? = Paper.book().read(REST_NOTE, "")

    fun writeSelectedAddress(value: LatLng) {
        Paper.book().write(SELECTED_ADDRESS, value)
    }

    fun readSelectedAddress(): LatLng? = Paper.book().read(SELECTED_ADDRESS)

    fun writeIsSelected(value: Int) {
        Paper.book().write(ISSelected, value)
    }

    fun readIsSelected(): Int? = Paper.book().read(ISSelected, 0)

    fun writeManageAddress(value: ManageAddress) {
        Paper.book().write(MANAGE_ADDRESS, value)
    }

    fun readManageAddress(): ManageAddress? = Paper.book().read(MANAGE_ADDRESS, ManageAddress())

    fun writeDeviceToken(value: String) {
        Paper.book().write(DEVICE_TOKEN, value)
    }

    fun readDeviceToken(): String? = Paper.book().read(DEVICE_TOKEN, "")

    fun writeFromCart(value: Boolean) {
        Paper.book().write(FROM_CART, value)
    }

    fun readFromCart(): Boolean? = Paper.book().read(FROM_CART, false)

    fun writeZoneId(value: Int) {
        Paper.book().write(ZONE_ID, value)
    }

    fun readZoneId(): Int? = Paper.book().read(ZONE_ID, 0)

    fun writeSearchRecent(value : MutableList<String>) {
        Paper.book().write(SEARCH_RECENT,value)
    }
    fun readSearchRecent(): MutableList<String> {
        return Paper.book().read(SEARCH_RECENT) ?: mutableListOf()
    }
    fun writeCurrencyVO(value : CurrencyVO) {
        Paper.book().write(CURRENCY_VO,value)
    }

    fun readCurrCurrency() : CurrencyVO? = Paper.book().read(CURRENCY_VO, CurrencyVO())

   /* fun writeCurrencyId(value: CurrencyVO) {
        Paper.book().write(CURRENCY_ID, value)
    }

    fun readCurrencyId(): CurrencyVO? = Paper.book().read(CURRENCY_ID,
        CurrencyVO()
    )*/

    fun writeParcelZoneId(value: Int) {
        Paper.book().write(PARCEL_ZONE_ID, value)
    }

    fun readParcelZoneId(): Int? = Paper.book().read(
        PARCEL_ZONE_ID,
        0
    )


    fun writeCustomerOrderId(value: Int) {
        Paper.book().write(CUSTOMER_ORDER_ID, value)
    }

    fun readCustomerOrderId(): Int? = Paper.book().read(
        CUSTOMER_ORDER_ID,
        0
    )




    fun clearCache() {
        writeUserVO(CustomerVO())
        writeFirstTime(true)
        writeLanguage("en")
        writeFoodOrderList(mutableListOf())
        writeAddToCart(false)
        writeRestaurant(FoodMenuByRestaurantVO())
        writeSenderReceiver(ParcelSenderReceiverVO())
        writeParcelInfo(ParcelInfoVO())
        writeIsSelected(0)
    }

    fun clearCartData() {
        writeRestaurant(FoodMenuByRestaurantVO())
        writeFoodOrderList(mutableListOf())
        writeIsSelected(0)
        writeSelectedAddress(LatLng(0.0, 0.0))
        writeRestaurantNote("")
        writeAddToCart(false)
        cartCount.postValue(0)

    }

    fun writeCurrencyType(value: String) {
        Paper.book().write(CURRENCY_TYPE, value)
    }

    fun readCurrencyType(): String? = Paper.book().read(CURRENCY_TYPE, "")

    fun writeHomeCity(value: Int) {
        Paper.book().write(HOME_CITY_ID, value)
    }

    fun readHomeCity(): Int? = Paper.book().read(HOME_CITY_ID, 0)


    fun writeChooseParcelAddress(value: Pair<Int, Int>) {
        Paper.book().write(CHOOSE_PARCEL_ADDRESS, value)
    }

    fun readChooseParcelAddress(): Pair<Int, Int>? =
        Paper.book().read(CHOOSE_PARCEL_ADDRESS, Pair(0, 0))


    const val default_image_url = "https://www.kasandbox.org/programming-images/avatars/duskpin-sapling.png"
}