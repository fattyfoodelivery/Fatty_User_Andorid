package com.orikino.fatty.ui.views.activities.checkout

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.orikino.fatty.domain.viewstates.OrderViewState
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.orikino.fatty.R
import com.orikino.fatty.adapters.FoodOrderAdapter
import com.orikino.fatty.adapters.PaymentMethodAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityCheckOutBinding
import com.orikino.fatty.databinding.LayoutDialogEditPhoneBinding
import com.orikino.fatty.databinding.LayoutDialogRemoveCartBinding
import com.orikino.fatty.domain.model.CreateFoodOrderVO
import com.orikino.fatty.domain.model.CreateFoodVO
import com.orikino.fatty.domain.model.CustomerAddressVO
import com.orikino.fatty.domain.model.FoodMenuByRestaurantVO
import com.orikino.fatty.domain.model.KPayResponseVO
import com.orikino.fatty.domain.view_model.OrderViewModel
import com.orikino.fatty.domain.viewstates.AddressViewState
import com.orikino.fatty.ui.views.activities.account_setting.manage_address.AddressDefinedActivity
import com.orikino.fatty.ui.views.activities.account_setting.manage_address.ManageAddressActivity
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.ui.views.activities.base.MainActivity
import com.orikino.fatty.ui.views.activities.rest_detail.RestaurantDetailViewActivity
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.ui.views.fragments.HomeFragment
import com.orikino.fatty.utils.ConfirmAddressDialog
import com.orikino.fatty.utils.Constants
import com.orikino.fatty.utils.CustomToast
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.GpsTracker
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.SHA
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import com.orikino.fatty.utils.helper.toDefaultRestaurantName
import com.orikino.fatty.utils.helper.toThousandSeparator
import com.orikino.fatty.utils.viewpod.EmptyViewPodDelegate
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CheckOutActivity : AppCompatActivity(), EmptyViewPodDelegate {

    private lateinit var binding: ActivityCheckOutBinding

    lateinit var foodOrderAdapter: FoodOrderAdapter
    lateinit var paymentMethodAdapter: PaymentMethodAdapter

    private var itemTotalPrice = 0.0
    private var billTotalPrice = 0.0
    private var qty = 0
    var aa = mutableListOf<CustomerAddressVO>()
    private var total = 0.0
    private var lastSelected = 0
    private var lat = 0.0
    private var lng = 0.0
    private var addressId = 0
    private var address = ""
    private var addressType = ""

    private var abnormalFee : Double = 0.0

    private var isRemove = false

    // Dev
    // private var signKey = "Fattyfood123456"
    // Pro
    private var signKey = "85bb9b77fa45f1d85cc3e70ee0e3e97c"

    // Dev
    // private var merchantCode = "200199"
    // Production
    private var merchantCode = "200135"

    // Dev
    // private var appId = "kpa5230efdfc0b4fc7a69b5ed348b597"
    // Pro
    private var appId = "kpbdd5ce1083eb4eb0b1d3b1effb0137"
    private var nonceStr = ""
    private var signType = "SHA256"
    private var orderInfo = ""
    private var forShowAddress = ""
    private var sign = ""
    private var dialogView: View? = null
    private var alertDialog: AlertDialog? = null
    private var addresses: List<Address> = listOf()
    private var result = mutableListOf<CreateFoodVO>()
    private var transformList = mutableListOf<CreateFoodOrderVO>()
    private val viewModel: OrderViewModel by viewModels()
    private var isSelected = false
    private var isBack = false
    private var phoneNo: String? = null
    var isCheckCashPayment = true
    private var defaultAddress : CustomerAddressVO? = null

    companion object {

        const val LAT = "lat"
        const val LNG = "lng"
        const val ADDRESS_ID = "address-id"
        const val ADDRESS = "address"
        const val ADDRESS_TYPE = "address-type"
        const val IS_Selected = "is-selected"
        const val IS_BACK = "is-back"
        const val PHONE_NO = "phone-no"

        fun getIntent(latitude : Double,longitude : Double,address_type : String): Intent {
            val intent = Intent(FattyApp.getInstance(), CheckOutActivity::class.java)
            intent.putExtra(LAT,latitude)
            intent.putExtra(LNG,longitude)
            intent.putExtra(ADDRESS_TYPE,address_type)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // from main activity
        lat = intent.getDoubleExtra(LAT, 0.0)
        lng = intent.getDoubleExtra(LNG, 0.0)
        addressType = intent.getStringExtra(ADDRESS_TYPE).toString()


        isSelected = intent.getBooleanExtra(IS_Selected, false)
        viewModel.lat = if (lat == 0.0) PreferenceUtils.readUserVO()?.latitude ?: 0.0 else lat
        viewModel.lng = if (lng == 0.0) PreferenceUtils.readUserVO()?.longitude ?: 0.0 else lng
        isSelected = intent.getBooleanExtra(IS_Selected, false)
        isBack = intent.getBooleanExtra(IS_BACK, false)
        addressId = intent.getIntExtra(ADDRESS_ID, 0)
        viewModel.addressID = addressId
        address = intent.getStringExtra(ADDRESS).toString()

        phoneNo = intent.getStringExtra(PHONE_NO)
        if (phoneNo == null) viewModel.customerAddressPhone = PreferenceUtils.readUserVO()?.customer_phone
        else viewModel.customerAddressPhone = phoneNo


        setUpFoodOrderRecyclerView()
        LoadingProgressDialog.showLoadingProgress(this)
        subscribeUI()
        setupEmptyView()
        bindRestInfo()
        navigateToPlacedOrderView()
        editAddress()
        onBackPress()
        setUpRestNote()
        setUpPhone()

        navigatePaymentCheck()

    }

    private fun setUpPhone() {
        //binding.tbtnEditPhone.gone()
        binding.btnEditPhone.setOnClickListener {
            if (defaultAddress != null){
                showPhoneEditDialog(false)
            }else{
                Toast.makeText(this, getString(R.string.txt_phone_edit_error),
                    Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnEditOtherPhone.setOnClickListener {
            if (defaultAddress != null){
                showPhoneEditDialog(true)
            }else{
                Toast.makeText(this, getString(R.string.txt_phone_edit_error),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPhoneEditDialog(isOtherPhone : Boolean){
        val dialogView =
            LayoutDialogEditPhoneBinding.inflate(LayoutInflater.from(this@CheckOutActivity))
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView.root)
        dialogView.tvDesc.text = if (isOtherPhone){
            getString(R.string.txt_current_phone_number, defaultAddress?.secondary_phone)
        }else{
            getString(R.string.txt_current_phone_number, defaultAddress?.customer_phone)
        }
        alertDialog = builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(true)
            dialogView.ivClose.setOnClickListener {
                dismiss()
            }
            dialogView.btnCancel.setOnClickListener {
                dismiss()
            }
            dialogView.btnUpdate.setOnClickListener {
                if (dialogView.edtPhone.text.toString().isNotEmpty()){
                    if (isOtherPhone){
                        defaultAddress?.let { data ->
                            viewModel.updateCurrentAddress(data.customer_address_id,data.customer_id,data.address_latitude,data.address_longitude,data.current_address,data.customer_phone,data.building_system?:"",data.address_type, data.is_default, dialogView.edtPhone.text.toString())
                        }
                    }else{
                        defaultAddress?.let { data ->
                            viewModel.updateCurrentAddress(data.customer_address_id,data.customer_id,data.address_latitude,data.address_longitude,data.current_address,dialogView.edtPhone.text.toString(),data.building_system?:"",data.address_type, data.is_default, data.secondary_phone)
                        }
                    }
                }else{
                    showSnackBar(getString(R.string.txt_please_enter_phone_number))
                }
                dismiss()
            }

            show()
        }
    }

    private fun checkGPS() {
        var gpsTracker = GpsTracker(this)
        if (!gpsTracker.canGetLocation()) gpsTracker.showSettingsAlert()
    }

    override fun onResume() {
        super.onResume()
        if (PreferenceUtils.readRestaurantNote() != "") binding.edtNote.setText(PreferenceUtils.readRestaurantNote())
//        viewModel.cartList.observe(this) {
//            PreferenceUtils.cartCount.postValue(it.size)
//            // rvCart.update(it)
//            println("createfoodvo $it")
//            foodOrderAdapter.setNewData(it)
//        }
        checkGPS()
        PreferenceUtils.readUserVO().customer_id?.let {
            viewModel.fetchCustomerAddressList(it)
        }
    }

    private fun onBackPress() {
        binding.ivBack.setOnClickListener {
            super.onBackPressed()
            PreferenceUtils.writeSelectedAddress(LatLng(0.0, 0.0))
            PreferenceUtils.writeIsSelected(0)
            PreferenceUtils.writeRestaurantNote("")
        }
    }

    private fun setUpRestNote() {
        binding.edtNote.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val trimmedNote = s?.toString()?.trim() ?: ""
                PreferenceUtils.writeRestaurantNote(trimmedNote)
                binding.tvNoteCount.text = "${trimmedNote.length}/30"
                if (trimmedNote.length > 30) {
                    binding.edtNote.clearFocus()
                    CustomToast(this@CheckOutActivity,
                        getString(R.string.your_note_exceeds_30_characters),false).createCustomToast()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun bindRestInfo() {
        binding.tvRestName.text = PreferenceUtils.readRestaurant()?.toDefaultRestaurantName()
        Picasso.get()
            .load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(PreferenceUtils.readRestaurant()?.restaurant_image))
            .placeholder(R.drawable.restaurant_default_img)
            .error(R.drawable.restaurant_default_img)
            .into(binding.ivRestaurant)
    }

    private fun navigateToPlacedOrderView() {
        binding.tvPlaceOrder.setOnClickListener {
            addressObserver()
            PreferenceUtils.readRestaurant()?.restaurant_id?.let { it1 ->
                viewModel.refreshFoodDeliveryFee(
                    viewModel.lat,
                    viewModel.lng,
                    it1,
                    itemTotalPrice
                )
            }
        }
    }

    private fun addressObserver() {
        viewModel.manageAddressLiveDataList.observe(this) {
            println("address list size ${it}")
            try {
                aa = it.filter {
                    it.is_default
                }.toMutableList()

                when (aa.size) {
                    0 -> {
                        //Old code removed, because these condition are not necessary only else state metter
                        /*when {
                            isSelected -> {
                                PreferenceUtils.readSelectedAddress()?.latitude?.let {
                                    viewModel.lat = it
                                }
                                PreferenceUtils.readSelectedAddress()?.longitude?.let {
                                    viewModel.lng = it
                                }
                                geoCodeLocationToAddress()
                            }

                            isBack -> {
                                if (PreferenceUtils.readSelectedAddress()?.latitude != 0.0 && PreferenceUtils.readSelectedAddress()?.longitude != 0.0) {
                                    PreferenceUtils.readSelectedAddress()?.latitude?.let {
                                        viewModel.lat = it
                                    }
                                    PreferenceUtils.readSelectedAddress()?.longitude?.let {
                                        viewModel.lng = it
                                    }
                                }
                                geoCodeLocationToAddress()
                            }

                            else -> {
                                val gpsTracker = GpsTracker(this)
                                if (gpsTracker.canGetLocation()) {
                                    viewModel.lat = gpsTracker.latitude
                                    viewModel.lng = gpsTracker.longitude
                                    geoCodeLocationToAddress()
                                }
                            }
                        }*/
                        val gpsTracker = GpsTracker(this)
                        if (gpsTracker.canGetLocation()) {
                            viewModel.lat = gpsTracker.latitude
                            viewModel.lng = gpsTracker.longitude
                            geoCodeLocationToAddress()
                        }

                        //set to default login phone for address not selected
                        binding.tvCurrentPhone.text = viewModel.customerAddressPhone
                        defaultAddress = null
                    }

                    else -> {
                        //Old code removed, because these condition are not necessary only else state metter
                        /* when {
                             isSelected -> {
                                 PreferenceUtils.readSelectedAddress()?.latitude?.let {
                                     viewModel.lat = it
                                 }
                                 PreferenceUtils.readSelectedAddress()?.longitude?.let {
                                     viewModel.lng = it
                                 }
                                 geoCodeLocationToAddress()
                             }

                             isBack -> {
                                 if (PreferenceUtils.readSelectedAddress()?.latitude == 0.0 && PreferenceUtils.readSelectedAddress()?.longitude == 0.0) {
                                     viewModel.lat = aa[0].address_latitude
                                     viewModel.lng = aa[0].address_longitude
                                 } else {
                                     PreferenceUtils.readSelectedAddress()?.latitude?.let {
                                         viewModel.lat = it
                                     }
                                     PreferenceUtils.readSelectedAddress()?.longitude?.let {
                                         viewModel.lng = it
                                     }
                                 }
                                 geoCodeLocationToAddress()
                             }

                             else -> {
                                 viewModel.lat = aa[0].address_latitude
                                 viewModel.lng = aa[0].address_longitude
                                 geoCodeLocationToAddress()
                             }
                         }*/
                        viewModel.lat = aa[0].address_latitude
                        viewModel.lng = aa[0].address_longitude
                        binding.tvCurrentPhone.text = aa[0].customer_phone
                        if (aa[0].secondary_phone != null){
                            binding.llOtherPhone.visibility = View.VISIBLE
                            binding.tvOtherPhone.text = aa[0].secondary_phone
                        }else{
                            binding.llOtherPhone.visibility = View.GONE
                        }

                        defaultAddress = aa[0]
                        //geoCodeLocationToAddress()
                        showAddress(defaultAddress?.current_address!!,defaultAddress?.building_system ?: "")
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun geoCodeLocationToAddress() {
        try {
            val geocoder = Geocoder(this, Locale.US)
            addresses = geocoder.getFromLocation(viewModel.lat, viewModel.lng, 1)!!
            showAddress(addresses[0].getAddressLine(0), "")
        } catch (e: Exception) {
            Log.d("Exception", e.message.toString())
        }
    }

    private fun showAddress(address: String, buildingSystem : String) {
        forShowAddress = address
        viewModel.address = address
        val address = if (buildingSystem.isNotEmpty()){
            "${address}\n${buildingSystem}"
        }else{
            address
        }
        binding.tvCurrentAddress.text = address
        binding.tvAddressType.text = if (addressType == "") "Other" else addressType
        if (defaultAddress == null){
            binding.btnEditPhone.gone()
        }else{
            binding.btnEditPhone.show()
        }
    }

    private fun createTimestamp(): String {
        val cal = Calendar.getInstance()
        val time = (cal.timeInMillis / 1000).toDouble()
        val d = java.lang.Double.valueOf(time)
        return Integer.toString(d.toInt())
    }

    private fun transformData(): String {
        transformList.clear()
        viewModel.cartList.value?.forEach {
            transformList.add(
                CreateFoodOrderVO(
                    it.food_id,
                    it.food_qty,
                    it.food_note,
                    it.food_price,
                    it.sub_item
                )
            )
        }
        return Gson().toJson(transformList)
    }

    private fun editAddress() {
        binding.rlManageAddress.setOnClickListener {
           // finish()
            startActivity(ManageAddressActivity.getIntent(true))
        }
        binding.tbtEditAddress.setOnClickListener {
           // finish()
            startActivity(ManageAddressActivity.getIntent(true))
        }
//        binding.tbnAddMore.setOnClickListener {
//            /*startActivity<RestaurantDetailViewActivity>(
//                RestaurantDetailViewActivity.RESTAURANT_ID to PreferenceUtils.readRestaurant()?.restaurant_id
//            )*/
//            val intent = Intent(this,RestaurantDetailViewActivity::class.java)
//            intent.putExtra(RestaurantDetailViewActivity.RESTAURANT_ID,PreferenceUtils.readRestaurant()?.restaurant_id)
//            startActivity(intent)
//        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeUI() {
        //viewModel.fetchPaymentList()
        viewModel.viewState.observe(this) { render(it) }
        viewModel.addressViewState.observe(this){
            renderEditPhone(it)
        }
        viewModel.cartList.observe(this) {
            if (it.isNullOrEmpty()) {
                if (isRemove){
                    finish()
                }else{
                    PreferenceUtils.writeRestaurant(FoodMenuByRestaurantVO())
                    showEmptyView()
                }
            } else {
                result = it
                itemTotalPrice = 0.0
                hideEmptyView()
                it.forEach {
                    itemTotalPrice += it.food_price
                }

                // TODO
                binding.tvItemTotal.text =
                    "${
                        itemTotalPrice.toThousandSeparator()
                    } ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
                billTotalPrice = itemTotalPrice.plus(viewModel.deliveryFee)
                binding.tvBillTotalPrice.text = "${billTotalPrice.toThousandSeparator()} ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
                binding.tvTotal.text = "${billTotalPrice.toThousandSeparator()} ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
                PreferenceUtils.readRestaurant()?.restaurant_id?.let { it1 ->


                    viewModel.foodDeliveryFee(
                        viewModel.lat,
                        viewModel.lng,
                        it1,
                        itemTotalPrice
                    )
                    println("viewModel lat lng ${viewModel.lat} and ${viewModel.lng} And ")

                }

                foodOrderAdapter.setNewData(it)
                println("viewModel cartlist result $result")
            }
        }
        addressObserver()
    }

    private fun renderEditPhone(state : AddressViewState){
        when(state){
            is AddressViewState.OnLoadingAddCurrentAddress -> renderOnLoadingAddAddress()
            is AddressViewState.OnSuccessAddCurrentAddress -> renderOnSuccessAddCurrentAddress(state)
            is AddressViewState.OnFailAddCurrentAddress -> renderOnFailAddAddress(state)
            else -> {}
        }
    }

    private fun renderOnLoadingAddAddress() {
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun renderOnSuccessAddCurrentAddress(state: AddressViewState.OnSuccessAddCurrentAddress) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            PreferenceUtils.readUserVO().customer_id?.let {
                viewModel.fetchCustomerAddressList(it)
            }
        }
    }

    private fun renderOnFailAddAddress(state: AddressViewState.OnFailAddCurrentAddress) {
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {
            "Server Issue" -> {
                showSnackBar("Server Issue")
            }

            "Another Login" -> {
                WarningDialog.Builder(
                    this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        //startActivity<SplashActivity>()
                        val intent = Intent(this, SplashActivity::class.java)
                        startActivity(intent)

                    })
                    .show(supportFragmentManager, AddressDefinedActivity::class.simpleName)
            }

            "Denied" -> WarningDialog.Builder(
                this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                })
                .show(supportFragmentManager, AddressDefinedActivity::class.simpleName)

            else -> {
                showSnackBar(state.message)
            }
        }
    }

    private fun render(state: OrderViewState) {
        when (state) {
            is OrderViewState.OnSuccessFoodDeliveryFee -> renderOnSuccessFoodDeliveryFee(state)

            is OrderViewState.OnFailFoodDeliveryFee -> renderOnFailFoodDeliveryFee(state)

            is OrderViewState.OnSuccessPaymentMethod -> renderOnSuccessPaymentMethod(state)
            is OrderViewState.OnFailPaymentMethod -> renderOnFailPaymentMethod(state)

            is OrderViewState.OnSuccessManageAddressList -> renderOnSuccessCustomerAddressList(state)

            is OrderViewState.OnFailManageAddressList -> renderOnFailCustomerAddressList(state)

            is OrderViewState.OnLoadingCreateFoodOrder -> renderOnLoadingCreateFoodOrder()
            is OrderViewState.OnSuccessCreateFoodOrder -> renderOnSuccessCreateFoodOrder(state)

            is OrderViewState.OnFailCreateFoodOrder -> renderOnFailCreateFoodOrder(state)

            is OrderViewState.OnLoadingRefreshFoodDeliveryFee -> renderOnLoadingRefreshDeliveryFee()
            is OrderViewState.OnSuccessRefreshFoodDeliveryFee -> renderOnSuccessRefreshDeliveryFeee(state)

            is OrderViewState.OnFailRefreshFoodDeliveryFee -> renderOnFailRefreshDeliveryFee(state)

            else -> {}
        }
    }

    private fun renderOnSuccessCreateFoodOrder(state: OrderViewState.OnSuccessCreateFoodOrder) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            cleanCartCache()
            if (state.data.data?.response?.code == "0") {
                bindOrderInfo(state.data.data?.response!!)
                finish()

                startActivity(state.data.data?.order?.customer_order_id.toString().let {
                    PlaceOrderActivity.getIntent(
                        it,true,orderInfo,sign,signType)
                })

            } else {
                finish()
                /*startActivity<PlaceOrderActivity>(
                    PlaceOrderActivity.ORDER_ID to state.data.data?.order?.order_id,
                    PlaceOrderActivity.IS_KPAY to false,
                )*/
                val intent = Intent(this,PlaceOrderActivity::class.java)
                intent.putExtra(PlaceOrderActivity.ORDER_ID,state.data.data?.order?.customer_order_id)
                intent.putExtra(PlaceOrderActivity.IS_KPAY,false)
                startActivity(intent)
            }
        } else {
            showSnackBar(resources.getString(R.string.fail_to_create))
        }
    }

    private fun cleanCartCache() {
        viewModel.cartList.postValue(mutableListOf())
        PreferenceUtils.writeRestaurant(FoodMenuByRestaurantVO())
        PreferenceUtils.writeFoodOrderList(mutableListOf())
        PreferenceUtils.writeIsSelected(0)
        PreferenceUtils.writeSelectedAddress(LatLng(0.0, 0.0))
        PreferenceUtils.writeRestaurantNote("")
        PreferenceUtils.writeAddToCart(false)
    }

    private fun renderOnFailRefreshDeliveryFee(state: OrderViewState.OnFailRefreshFoodDeliveryFee) {
        LoadingProgressDialog.hideLoadingProgress()
        when(state.message){
            "Server Issue" -> {
                showSnackBar("Server Err 500")
            }

            "Another Login" -> WarningDialog.Builder(this,
                resources.getString(R.string.already_login_title),
                resources.getString(R.string.already_login_msg),
                resources.getString(R.string.force_login),
                callback = {
                    PreferenceUtils.clearCache()
                    finish()
                    //startActivity<SplashActivity>()
                    val intent = Intent(this,SplashActivity::class.java)
                    startActivity(intent)
                }).show(supportFragmentManager, HomeFragment::class.simpleName)


            "Denied" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                })
                .show(this.supportFragmentManager, SplashActivity::class.simpleName)

            "Failed" -> { showSnackBar(state.message) }
            else -> {
                showSnackBar(state.message!!)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun renderOnSuccessRefreshDeliveryFeee(state: OrderViewState.OnSuccessRefreshFoodDeliveryFee) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            abnormalFee = state.data.data.abnormal_fee
            if (state.data.data.abnormal_fee > 0) {
                binding.rlAbnormal.show()
            } else {
                binding.rlAbnormal.gone()
            }
            binding.tvDeliveryFee.text = "${
                state.data.data.delivery_fee.toThousandSeparator()
            } ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
            binding.tvAbnormalFee.text = "${
                abnormalFee.toThousandSeparator()
            } ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"

            viewModel.deliveryFee = state.data.data.delivery_fee
            viewModel.deliveryFeeOrigin = state.data.data.delivery_fee_origin

            var calculatedBillTotalPrice = itemTotalPrice.plus(state.data.data.delivery_fee).plus(abnormalFee)

            if (PreferenceUtils.readCurrCurrency()?.currency_symbol == "MMK"){ // --- Start of your custom rounding logic ---
                val remainder = calculatedBillTotalPrice % 100
                val base =
                    calculatedBillTotalPrice - remainder // Gets the hundreds part (e.g., 201 -> 200)

                calculatedBillTotalPrice = if (remainder > 0 && remainder <= 50) {
                    base + 50
                } else if (remainder > 50) {
                    base + 100
                } else { // This handles cases where remainder is 0 (e.g. 200, 300) or exactly 50
                    if (remainder == 0.0) base else base + 50 // if exactly 0, keep as is, if 50, make it 50
                }
                // --- End of your custom rounding logic ---}
            }

            billTotalPrice = calculatedBillTotalPrice // Assign the modified value

            binding.tvBillTotalPrice.text =
                "${billTotalPrice.toThousandSeparator()} ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
            binding.tvTotal.text =
                "${billTotalPrice.toThousandSeparator()} ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
           // binding.llNoti.gone()
            if (state.data.data.is_abnormal){
                binding.llAbnormal.show()
                binding.tvAbnormalAlert.text = state.data.data.abnormal_alert
            }else{
                binding.llAbnormal.gone()
            }
            if (state.data.data.is_peaktime){
                binding.llPeakTime.show()
                binding.tvPeakTimeAlert.text = state.data.data.peaktime_alert
            }else{
                binding.llPeakTime.gone()
            }
//            billTotalPrice = itemTotalPrice.plus(state.data.data.delivery_fee).plus(abnormalFee)
//            binding.tvBillTotalPrice.text =
//                "${billTotalPrice.toThousandSeparator()} ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
//            binding.tvTotal.text =
//                "${billTotalPrice.toThousandSeparator()} ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"

            // origin code
            /*if (state.data.data.delivery_fee > 0) {
                tv_notice.visibility = View.VISIBLE
                when {
                    state.data.data.restaurant_delivery_fee <= 0 -> tv_notice.text =
                        when (Preference.readLanguage()) {
                            "my" -> {
                                "*${state.data.data.system_deli_distance} km အတွင်း ပိုဆောင်ခ အခမဲ့*"
                            }
                            "en" -> {
                                "*Free Delivery in ${state.data.data.system_deli_distance} km*"
                            }
                            else -> {
                                "*${state.data.data.system_deli_distance} km 以内免费派送*"
                            }
                        }
                    else -> tv_notice.text = when (Preference.readLanguage()) {
                        "my" -> {
                            "*${
                                state.data.data.define_amount.toDouble().toThousandSeparator()
                            } ကျပ်အထက်နှင့် ${state.data.data.system_deli_distance} km အတွင်းပိုဆောင်ခ အခမဲ့*"
                        }
                        "en" -> {
                            "*Free Delivery above ${
                                state.data.data.define_amount.toDouble().toThousandSeparator()
                            } ${Preference.readCurrencyId().currency_symbol} in ${state.data.data.system_deli_distance} km*"
                        }
                        else -> {
                            "*${
                                state.data.data.define_amount.toDouble().toThousandSeparator()
                            }  起 ${state.data.data.system_deli_distance} km 以内 免费派送*"
                        }
                    }
                }

            } else tv_notice.visibility = View.GONE*/

            LoadingProgressDialog.hideLoadingProgress()

            showAddressPickDialog(
                resources.getString(R.string.confirm_address),
                when {
                    PreferenceUtils.readLanguage() == "my" -> "${resources.getString(
                        R.string.confirm_address_message
                    )} <br><br>လက်ရှိလိပ်စာ - ${addressBold()}</br></br>"
                    PreferenceUtils.readLanguage() == "en" -> "${resources.getString(
                        R.string.confirm_address_message
                    )} <br><br>Current Address - ${addressBold()}</br></br>"
                    else -> "${resources.getString(
                        R.string.confirm_address_message
                    )} <br><br>当前位置 - ${addressBold()}</br></br>"
                }
            )
        }
    }

    private fun addressBold(): String {
        return "<b>$forShowAddress</b> "
    }

    private fun showAddressPickDialog(title: String, message: String) {
        val dialogView =
            LayoutDialogRemoveCartBinding.inflate(LayoutInflater.from(this@CheckOutActivity))
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView.root)
        alertDialog = builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(true)
            dialogView.tvTitle?.text = title
            dialogView.tvTitleDesc?.text = Html.fromHtml(message)
            dialogView.btnClose?.text = resources.getString(R.string.other_address)

            dialogView?.btnClose?.setOnClickListener {
                dismiss()
                //finish()
                startActivity(ManageAddressActivity.getIntent(true))
            }
            dialogView?.btnRemove?.text = resources.getString(R.string.confirm)
            dialogView?.btnRemove?.setOnClickListener {
                PreferenceUtils.readUserVO()?.customer_id?.let { customerId ->
                    PreferenceUtils.readRestaurant()?.restaurant_id?.let { restaurantId ->
                        PreferenceUtils.readRestaurantNote()?.let { note ->
                            viewModel.createFoodOrder(
                                customerId,
                                restaurantId,
                                note,
                                transformData(),
                                defaultAddress?.customer_address_id ?: 0,
                                viewModel.deliveryFeeOrigin,
                                viewModel.deliveryFee,
                                itemTotalPrice,
                                billTotalPrice,
                                viewModel.lat,
                                viewModel.lng,
                                viewModel.paymentMethodID,
                                viewModel.address,
                                if (defaultAddress == null){
                                    viewModel.customerAddressPhone ?: ""
                                }else{
                                    defaultAddress?.customer_phone ?: ""
                                },
                                abnormalFee,
                                if (defaultAddress == null){
                                    null
                                }else{
                                    defaultAddress?.secondary_phone
                                }
                            )
                        }
                    }
                }
                dismiss()
            }

            show()
        }
    }

    private fun bindOrderInfo(data: KPayResponseVO) {
        val timestamp = createTimestamp()
        orderInfo =
            (
                "appid=" + appId + "&merch_code=" + merchantCode + "&nonce_str=" + data.nonce_str + "&prepay_id=" +
                    data.prepay_id + "&timestamp=" + timestamp
                )
        sign = SHA.getSHA256Str("$orderInfo&key=$signKey").uppercase()
        //sign = data.sign
    }

    private fun renderOnLoadingCreateFoodOrder() {
        LoadingProgressDialog.showLoadingProgress(this)
    }

    private fun renderOnFailCustomerAddressList(state: OrderViewState.OnFailManageAddressList) {
        LoadingProgressDialog.hideLoadingProgress()
        when(state.message){
            "Server Issue" -> {
                showSnackBar("Server Error 500")
            }

            "Another Login" ->
                WarningDialog.Builder(this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finishAffinity()
                        //startActivity<SplashActivity>()
                        val intent = Intent(this,SplashActivity::class.java)
                        startActivity(intent)
                    }).show(supportFragmentManager, CheckOutActivity::class.simpleName)


            "Denied" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                })
                .show(this.supportFragmentManager, SplashActivity::class.simpleName)

            "Failed" -> { showSnackBar(state.message) }
            else -> {
                showSnackBar(state.message!!)
            }
        }
    }

    private fun renderOnSuccessCustomerAddressList(state: OrderViewState.OnSuccessManageAddressList) {
        if (state.data.success) {
            LoadingProgressDialog.hideLoadingProgress()
            viewModel.manageAddressLiveDataList.postValue(state.data.data)
        }
    }

    private fun renderOnFailPaymentMethod(state: OrderViewState.OnFailPaymentMethod) {
        LoadingProgressDialog.hideLoadingProgress()
        when(state.message){
            "Server Issue" -> {
                showSnackBar("Server Error 500")
            }

            "Another Login" ->
                WarningDialog.Builder(this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finishAffinity()
                        //startActivity<SplashActivity>()
                        val intent = Intent(this,SplashActivity::class.java)
                        startActivity(intent)
                    }).show(supportFragmentManager, CheckOutActivity::class.simpleName)


            "Denied" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                })
                .show(this.supportFragmentManager, SplashActivity::class.simpleName)

            "Failed" -> { showSnackBar(state.message) }
            else -> {
                showSnackBar(state.message!!)
            }
        }
    }

    private fun renderOnSuccessPaymentMethod(state: OrderViewState.OnSuccessPaymentMethod) {
        if (state.data.success) {
            LoadingProgressDialog.hideLoadingProgress()
            viewModel.shouldFetchData = false
            viewModel.paymentMethodID = state.data.data[0].payment_method_id
        }
    }

    private fun checkUpdate(check : Boolean) {
        //updateObserver()
        if (check) {
            isCheckCashPayment = true
            cashCheck()
        } else {
            isCheckCashPayment = false
            kpayCheck()
        }
    }
    private fun navigatePaymentCheck() {
        binding.rlCash.setOnClickListener {
            viewModel.paymentMethodID = 1
            checkUpdate(true)
        }

        binding.rlKpay.setOnClickListener {
            viewModel.paymentMethodID = 2
            checkUpdate(false)
        }
    }

    private fun kpayCheck() {
        binding.rlKpay.background =
            ContextCompat.getDrawable(this@CheckOutActivity, R.drawable.bg_selected)
        binding.rbtCheckKpay.setImageResource(R.drawable.radio_check)
        binding.rlCash.background =
            ContextCompat.getDrawable(this@CheckOutActivity, R.drawable.bg_unselectd)
        binding.rbtCheckCash.setImageResource(R.drawable.radio_uncheck)
    }

    private fun cashCheck() {
        binding.rlKpay.background =
            ContextCompat.getDrawable(this@CheckOutActivity, R.drawable.bg_unselectd)
        binding.rbtCheckKpay.setImageResource(R.drawable.radio_uncheck)
        binding.rlCash.background =
            ContextCompat.getDrawable(this@CheckOutActivity, R.drawable.bg_selected)
        binding.rbtCheckCash.setImageResource(R.drawable.radio_check)
    }

    private fun renderOnFailFoodDeliveryFee(state : OrderViewState.OnFailFoodDeliveryFee) {
        LoadingProgressDialog.hideLoadingProgress()
    }

    @SuppressLint("SetTextI18n")
    private fun renderOnSuccessFoodDeliveryFee(state: OrderViewState.OnSuccessFoodDeliveryFee) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success) {
            abnormalFee = state.data.data.abnormal_fee
            if (state.data.data.abnormal_fee > 0) {
                binding.rlAbnormal.show()
            } else {
                binding.rlAbnormal.gone()
            }
            binding.tvDeliveryFee.text = "${
                state.data.data.delivery_fee.toThousandSeparator()
            } ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
            viewModel.deliveryFee = state.data.data.delivery_fee
            viewModel.deliveryFeeOrigin = state.data.data.delivery_fee_origin
            billTotalPrice = itemTotalPrice.plus(state.data.data.delivery_fee)
            var calculatedBillTotalPrice = itemTotalPrice.plus(state.data.data.delivery_fee).plus(abnormalFee)

            if (PreferenceUtils.readCurrCurrency()?.currency_symbol == "MMK"){ // --- Start of your custom rounding logic ---
                val remainder = calculatedBillTotalPrice % 100
                val base =
                    calculatedBillTotalPrice - remainder // Gets the hundreds part (e.g., 201 -> 200)

                calculatedBillTotalPrice = if (remainder > 0 && remainder <= 50) {
                    base + 50
                } else if (remainder > 50) {
                    base + 100
                } else { // This handles cases where remainder is 0 (e.g. 200, 300) or exactly 50
                    if (remainder == 0.0) base else base + 50 // if exactly 0, keep as is, if 50, make it 50
                }
                // --- End of your custom rounding logic ---}
            }
            billTotalPrice = calculatedBillTotalPrice // Assign the modified value
            binding.tvBillTotalPrice.text = "${billTotalPrice.toThousandSeparator()} ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
            binding.tvTotal.text = "${billTotalPrice.toThousandSeparator()} ${PreferenceUtils.readCurrCurrency()?.currency_symbol}"
            //binding.llNoti.gone()
            if (state.data.data.is_abnormal){
                binding.llAbnormal.show()
                binding.tvAbnormalAlert.text = state.data.data.abnormal_alert
            }else{
                binding.llAbnormal.gone()
            }
            if (state.data.data.is_peaktime){
                binding.llPeakTime.show()
                binding.tvPeakTimeAlert.text = state.data.data.peaktime_alert
            }else{
                binding.llPeakTime.gone()
            }
            /*if (state.data.data.delivery_fee > 0) {
                tv_notice.visibility = View.VISIBLE
                when {
                    state.data.data.restaurant_delivery_fee <= 0 -> tv_notice.text =
                        when (Preference.readLanguage()) {
                            "my" -> {
                                "*${state.data.data.system_deli_distance} km အတွင်း ပိုဆောင်ခ အခမဲ့*"
                            }
                            "en" -> {
                                "*Free Delivery in ${state.data.data.system_deli_distance} km*"
                            }
                            else -> {
                                "*${state.data.data.system_deli_distance} km 以内免费派送*"
                            }
                        }
                    else -> tv_notice.text = when (Preference.readLanguage()) {
                        "my" -> {
                            "*${
                                state.data.data.define_amount.toDouble().toThousandSeparator()
                            } ကျပ်အထက်နှင့် ${state.data.data.system_deli_distance} km အတွင်းပိုဆောင်ခ အခမဲ့*"
                        }
                        "en" -> {
                            "*Free Delivery above ${
                                state.data.data.define_amount.toDouble().toThousandSeparator()
                            } ${Preference.readCurrencyId().currency_symbol} in ${state.data.data.system_deli_distance} km*"
                        }
                        else -> {
                            "*${
                                state.data.data.define_amount.toDouble().toThousandSeparator()
                            }  起 ${state.data.data.system_deli_distance} km 以内 免费派送*"
                        }
                    }
                }

            } else tv_notice.visibility = View.GONE*/

            if (viewModel.shouldFetchData) viewModel.fetchPaymentList()
        }
    }

    private fun renderOnLoadingRefreshDeliveryFee() {
        LoadingProgressDialog.hideLoadingProgress()
    }

    private fun renderOnFailCreateFoodOrder(state: OrderViewState.OnFailCreateFoodOrder) {
        LoadingProgressDialog.hideLoadingProgress()
        when(state.message){
            Constants.CONNECTION_ISSUE -> {
                showSnackBar(resources.getString(R.string.no_internet_title))
            }

            Constants.ANOTHER_LOGIN -> startActivity(LoginActivity.getIntent("checkout"))


            Constants.DENIED -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                })
                .show(this.supportFragmentManager, SplashActivity::class.simpleName)

            Constants.FAILED -> { showSnackBar(state.message) }
            else -> {
                showSnackBar(state.message!!)
            }
        }
    }

//    private fun confirmLocationDialog() {
//        val title = resources.getString(R.string.please_confirm_address)
//        val des = resources.getString(R.string.building_14_mict_park_hlaing_township)
//        val btn = resources.getString(R.string.confirm)
//        ConfirmAddressDialog.Builder(
//            FattyApp.getInstance(),
//            title,
//            des,
//            btn,
//            cancelCallback = {
//                startActivity(
//                    AddressPickUpMapBoxActivity.getIntent(
//                        resources.getString(R.string.add_new_address),
//                        2
//                    )
//                )
//            },
//            callback = {
//             //   startActivity(PlaceOrderActivity.getIntent())
//            }
//        ).show(supportFragmentManager, CheckOutActivity::class.java.simpleName)
//    }

    private fun setUpFoodOrderRecyclerView() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL, false)
        binding.rvFoodItem.layoutManager = linearLayoutManager
        binding.rvFoodItem.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding.rvFoodItem.setHasFixedSize(true)
        binding.rvFoodItem.isNestedScrollingEnabled = true
        foodOrderAdapter = FoodOrderAdapter(FattyApp.getInstance()) { data,str,pos ->
            when(str) {
                "add_more" -> {
                    PreferenceUtils.needToShow = false
                    /*startActivity<RestaurantDetailViewActivity>(
                        RestaurantDetailViewActivity.RESTAURANT_ID to data.restaurant_id
                    )*/
                    val intent = Intent(this,RestaurantDetailViewActivity::class.java)
                    intent.putExtra(RestaurantDetailViewActivity.RESTAURANT_ID,data.restaurant_id)
                    startActivity(intent)
                }
                "minus" -> itemMinusClick(data,pos)
                "plus" -> itemPlusClick(data,pos)
            }
        }
        binding.rvFoodItem.adapter = foodOrderAdapter
    }

    private fun itemMinusClick(data : CreateFoodVO,pos : Int) {
        try {
            if (data.food_qty == 1) {
                showConfirmDialog(
                    resources.getString(R.string.remove_from_cart),
                    resources.getString(R.string.are_you_sure),
                    data
                )
            } else {
                qty = data.food_qty.minus(1)
                var subItemPrice = 0.0
                data.sub_item.forEach {
                    it.option.forEach {
                        subItemPrice = subItemPrice.plus(it.food_sub_item_price)
                    }
                }
                total = (data.initial_price + subItemPrice) * qty
                result[pos].food_qty = qty
                result[pos].food_price = total
                PreferenceUtils.writeFoodOrderList(result)
                viewModel.cartList.postValue(PreferenceUtils.readFoodOrderList())

            }
        } catch (e: Exception) {
        }
    }

    private fun itemPlusClick(data: CreateFoodVO, pos: Int) {
        try {
            val newQty = data.food_qty.plus(1)
            var subItemPrice = 0.0
            data.sub_item.forEach { subItem ->
                subItem.option.forEach { option ->
                    subItemPrice = subItemPrice.plus(option.food_sub_item_price)
                }
            }
            val newTotal = (data.initial_price + subItemPrice) * newQty

            val currentList = viewModel.cartList.value?.toMutableList()

            if (currentList != null && pos >= 0 && pos < currentList.size) {
                // Create a new CreateFoodVO instance with the updated values
                val updatedItem = CreateFoodVO(
                    food_id = data.food_id,
                    food_name = data.food_name,
                    initial_price = data.initial_price,
                    food_qty = newQty, // Use the new quantity
                    food_image = data.food_image,
                    food_price = newTotal, // Use the new total price
                    food_note = data.food_note,
                    sub_item = data.sub_item // Assuming sub_item does not change here
                )

                // Replace the item at the same position
                currentList[pos] = updatedItem

                PreferenceUtils.writeFoodOrderList(currentList)
                // Post the updated list to LiveData; original code re-reads, so sticking to that pattern.
                viewModel.cartList.postValue(PreferenceUtils.readFoodOrderList())
            } else {
                // Handle the case where the list is null or position is out of bounds, if necessary
                // For example, you could log an error here.
            }
        } catch (e: Exception) {
            // Handle any exceptions, e.g., log the error
        }
    }



    private fun showConfirmDialog(title: String, message: String, data: CreateFoodVO) {
        val dialogView = LayoutDialogRemoveCartBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView.root)
        alertDialog = builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            dialogView.tvTitle.text = title
            dialogView.tvTitleDesc.text = message

            dialogView.btnClose.setOnClickListener {
                dismiss()
            }
            dialogView.btnRemove.text = resources.getString(R.string.confirm)
            dialogView.btnRemove.setOnClickListener {
                isRemove = true
                dismiss()
                result = viewModel.cartList.value?.filter {
                    it != data
                }?.toMutableList() ?: mutableListOf()

                PreferenceUtils.writeFoodOrderList(result)
                viewModel.cartList.postValue(PreferenceUtils.readFoodOrderList())
            }

            show()
        }
    }

    private fun setupEmptyView() {
        binding.emptyViewPod.root.setEmptyData(
            message = resources.getString(R.string.please_add_items_to_your_cart),"",
            resources.getDrawable(R.drawable.no_wifi_or_connection)
        )

        binding.emptyViewPod.root.setDelegate(this)
        binding.emptyViewPod.btnExpore.setOnClickListener {
            finishAffinity()
            MainActivity.isFirstTime = true
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun showEmptyView() {
        binding.emptyViewPod.root.show()
        binding.rlPlaceOrder.gone()
        binding.nestedVew.gone()
    }

    private fun hideEmptyView() {
        binding.emptyViewPod.root.gone()
        binding.rlPlaceOrder.show()
        binding.nestedVew.show()
    }
    override fun onTapTryAgain() {

    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}
