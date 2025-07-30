package com.orikino.fatty.ui.views.activities.track

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import coil.load
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ActivityTrackOrderMapBoxBinding
import com.orikino.fatty.databinding.TrackOrderBottomsheetBinding
import com.orikino.fatty.domain.model.ActiveOrderVO
import com.orikino.fatty.domain.model.FoodVO
import com.orikino.fatty.domain.view_model.TrackFoodOrderViewModel
import com.orikino.fatty.domain.viewstates.TrackFoodOrderViewState
import com.orikino.fatty.service.FattyPushyService
import com.orikino.fatty.ui.views.activities.account_setting.help_center.HelpCenterActivity
import com.orikino.fatty.ui.views.activities.base.MainActivity
import com.orikino.fatty.ui.views.activities.chat.ChattingActivity
import com.orikino.fatty.ui.views.activities.splash.SplashActivity
import com.orikino.fatty.utils.GpsTracker
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.phoneCall
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import com.orikino.fatty.utils.helper.toThousandSeparator
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class TrackOrderMapBoxActivity : AppCompatActivity()/*, OnMapReadyCallback*/ {


    private lateinit var binding : ActivityTrackOrderMapBoxBinding
    lateinit var tBind: TrackOrderBottomsheetBinding

    companion object {
        const val ORDER_ID = "order_id"
        const val IS_UPDATE_RIDER = "is-update-rider"
    }

    val UPDATE_INTERVAL = 30000L

    private var updateWidgetRunnable: Runnable = Runnable {
        run {
            //Update UI
            //to do update rider
            subscribeUI()
            // Re-run it after the update interval
            updateWidgetHandler.postDelayed(updateWidgetRunnable, UPDATE_INTERVAL)
        }

    }



    var location: Location = Location("")
    //private lateinit var mapBox: MapboxMap
    private val updateWidgetHandler = Handler()
    private var addresses: List<Address> = listOf()
    private var orderId: Int = 0
    private var riderId: Int = 0
    private var isUpdateRider = false
    private var total = 0.0
    private var billTotal = 0.0
    private var fooAddOnList = mutableListOf<String>()
    private lateinit var sheetBehavior: BottomSheetBehavior<*>
    private lateinit var bottom_sheet: ConstraintLayout
    private val viewModel: TrackFoodOrderViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*Mapbox.getInstance(
            this@TrackOrderMapBoxActivity,
            "sk.eyJ1IjoiZmF0dHlmb29kIiwiYSI6ImNsMTdqaXl6eDA0aGszZ280YzF1MXNjMHoifQ.d5Qvf_pXSsxX7nLjgVArFw"
        )*/
        binding = ActivityTrackOrderMapBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //binding.mapBoxMap.onCreate(savedInstanceState)
        //binding.mapBoxMap.getMapAsync(this)
        setUpBottomSheet()
        if (intent.hasExtra(ORDER_ID)) orderId = intent.getIntExtra(ORDER_ID, 0)
        isUpdateRider = intent.getBooleanExtra(IS_UPDATE_RIDER, false)
        contactToCallCenter()
        stopService()
        onBackPress()
    }



    private fun stopService() {
        val intent = Intent(this, FattyPushyService::class.java)
        stopService(intent)
    }

    private fun setUpBottomSheet() {

        sheetBehavior = BottomSheetBehavior.from(tBind.root)
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        tBind.llHeaderView.gone()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        tBind.llHeaderView.show()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        tBind.tbTPBack.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun callToRider(phone: String) {
        tBind.btnPhoneCall.setOnClickListener {
            phoneCall(phone)
        }
    }

    private fun contactToCallCenter() {
        binding.btnAgent.setOnClickListener {
            //startActivity<HelpCenterActivity>()
            val intent = Intent(this,HelpCenterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkGPS() {
        var gpsTracker = GpsTracker(this)
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert()
        }
    }

    override fun onStart() {
        super.onStart()
        //binding.mapBoxMap.onStart()
    }

    override fun onResume() {
        super.onResume()
        subscribeUI()
        updateWidgetHandler.postDelayed(updateWidgetRunnable, UPDATE_INTERVAL)
        //binding.mapBoxMap.onResume()
        checkGPS()
    }

    @SuppressLint("SetTextI18n")
    private fun bindOrderInfo(data: ActiveOrderVO) {
        binding.tvOrderTime.text = data.order_time
        tBind.tvEstimateTimeValue.text =
            if (data.estimated_start_time != null && data.estimated_end_time != null) "${data.estimated_start_time} - ${data.estimated_end_time}" else "02:45PM - 03:15PM"
        binding.tvOrderId.text =
            "${resources.getString(R.string.order_id)} | ${data.customer_order_id}"
        tBind.tvOrderIdSheetValue.text = data.customer_order_id
        tBind.tvBookingIdSheetValue.text = data.customer_booking_id

        tBind.tvCustomerAddressPhone.text = data.customer_address_phone

        when (data.order_status?.order_status_id) {
            1 -> tBind.tvFoodStatus.text = data.order_status?.order_status_name
            3 -> tBind.tvFoodStatus.text = data.order_status?.order_status_name
            4 -> {
                tBind.imvMotor.setColorFilter(
                    ContextCompat.getColor(this, R.color.fattyPrimary),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                tBind.tvFoodStatus.text = data.order_status?.order_status_name

            }
            5 -> {
                tBind.imvMotor.setColorFilter(
                    ContextCompat.getColor(this, R.color.fattyPrimary),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                //progress_rider_show.visibility = View.VISIBLE
                tBind.tvFoodStatus.text = data.order_status?.order_status_name
            }

            6 -> {
                tBind.imvMotor.setColorFilter(
                    ContextCompat.getColor(this, R.color.fatty),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                //progress_rider_show.visibility = View.VISIBLE
                tBind.tvFoodStatus.text = data.order_status?.order_status_name
            }
            7 -> {
                tBind.imvUser.setColorFilter(
                    ContextCompat.getColor(this, R.color.fatty),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                tBind.imvMotor.setColorFilter(
                    ContextCompat.getColor(this, R.color.fatty),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                //progress_rider_show.visibility = View.VISIBLE
                tBind.tvFoodStatus.text = data.order_status?.order_status_name
            }
            10 -> {
                tBind.imvMotor.setColorFilter(
                    ContextCompat.getColor(this, R.color.fatty),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                //progress_rider_show.visibility = View.VISIBLE
                tBind.tvFoodStatus.text = data.order_status?.order_status_name
            }
        }


        /*showAddress(
            convertLatLangToAddress(
                data.customer_address_latitude ?: 0.0,
                data.customer_address_longitude ?: 0.0
            )
        )*/

        setUpOrderItemRecycler(data.foods, data.currency_id)
        tBind.tvPaymentName.text = data.payment_method?.payment_method_name
        tBind.tvDeliveryFee.text =
            "${data.delivery_fee.toThousandSeparator()} ${if (data.currency_id == 1) "MMK" else "¥"}"
        total = 0.0
        billTotal = 0.0

        data.foods.forEach {
            total += it.food_price?.toDouble() ?: 0.0
        }

        data.foods.forEach {
            if (it.is_cancel == 0) billTotal += it.food_price?.toDouble() ?: 0.0
        }

        if (data.rider != null) {
            riderId = data.rider!!.rider_id
            viewModel.fetchRiderLocation(riderId)
            tBind.rider.show()
            callToRider(data.rider?.rider_user_phone ?: "")

            tBind.imvRiderPhoto.load(PreferenceUtils.IMAGE_URL.plus("/rider/").plus(data.rider?.rider_image)) {
                error(R.drawable.fatty_rounded)
                placeholder(R.drawable.fatty_rounded)
            }

            tBind.tvRiderName.text = data.rider?.rider_user_name
            viewModel.rider_latlng =
                LatLng(data.rider!!.rider_latitude, data.rider!!.rider_longitude)

        }

        tBind.tvTotalOriginalValue.text = "${
            total.plus(data.delivery_fee).toThousandSeparator()
        } ${if (data.currency_id == 1) "MMK" else "¥"}"
        tBind.tvTotalPrice.text =
            "${data.bill_total_price.toThousandSeparator()} ${if (data.currency_id == 1) "MMK" else "¥"}"
        viewModel.customer_latlng = LatLng(0.0,0.0)//LatLng(data.customer_address_latitude ?: 0.0, data.customer_address_longitude ?: 0.0)
        viewModel.restaurant_latlng = LatLng(
            data.restaurant?.restaurant_latitude ?: 0.0,
            data.restaurant?.restaurant_longitude ?: 0.0
        )

       // onMapReady(mapBox)

        tBind.imvSendMessage.setOnClickListener {
            /*startActivity<ChattingActivity>(
                ChattingActivity.RIDER_ID to data.rider!!.rider_id,
                ChattingActivity.ORDER to Gson().toJson(data)
            )*/
            val intent = Intent(this,ChattingActivity::class.java)
            intent.putExtra(ChattingActivity.RIDER_ID,data.rider!!.rider_id)
            intent.putExtra(ChattingActivity.ORDER,Gson().toJson(data))
        }
    }

    private fun convertLatLangToAddress(lat: Double, lng: Double): String {
        var address = ""
        try {

        } catch (e: Exception) {
            val geocoder = Geocoder(this, Locale.getDefault())
            addresses = geocoder.getFromLocation(lat, lng, 1)!!
            address = addresses[0].getAddressLine(0)
        }

        return address
    }

    private fun showAddress(address: String) {
        try {
            tBind.tvCurrentAddress.text = address
        } catch (e: Exception) {
        }
    }

    private fun setUpOrderItemRecycler(data: MutableList<FoodVO>, currencyId: Int) {
        /*rvItem.bind(data, R.layout.layout_item_order_info) { item, position ->
            bindItemData(item, currencyId)
        }.layoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        )*/
    }

    @SuppressLint("SetTextI18n")
    private fun View.bindItemData(data: FoodVO, currencyId: Int) {
        /*this.tv_food_name_qty.text = "${data.food_qty} × ${data.toDefaultFoodName()}"
        this.tv_amount.text = "${
            data.food_price?.toDouble()?.toThousandSeparator()
        } ${if (currencyId == 1) "MMK" else "¥"}"
        tv_food_note.text = data.food_note
        if (data.is_cancel != 0) this.tv_food_name_qty.setTextColor(Color.parseColor("#a9a9a9"))
        if (data.is_cancel != 0) this.tv_amount.setTextColor(Color.parseColor("#a9a9a9"))
        if (data.is_cancel != 0) this.tv_food_cancel.visibility = View.VISIBLE
        if (data.is_cancel != 0) this.tv_food_addon.setTextColor(Color.parseColor("#a9a9a9"))
        tv_food_note.text = data.food_note
        data.sub_item.forEach {
            it.option.forEach { optionVO ->
                optionVO.toDefaultOptionName()?.let { it1 -> fooAddOnList.add(it1) }
            }
        }
        this.tv_food_addon.text = fooAddOnList.joinToString {
            "${it}"
        }
        fooAddOnList.clear()*/
    }

    private fun subscribeUI() {
        viewModel.trackFoodOrder(orderId)
        viewModel.viewState.observe(this, { render(it) })
        viewModel.trackOrderLiveData.observe(this) {
            bindOrderInfo(it)
        }
    }

    private fun render(state: TrackFoodOrderViewState) {
        when (state) {
            is TrackFoodOrderViewState.OnLoadingTrackFoodOrder -> renderOnLoadingTrackFoodOrder()
            is TrackFoodOrderViewState.OnSuccessTrackFoodOrder -> renderOnSuccessTrackFoodOrder(
                state
            )
            is TrackFoodOrderViewState.OnFailTrackFoodOrder -> renderOnFailTrackFoodOrder(state)
            is TrackFoodOrderViewState.OnSuccessFetchRiderLocation -> renderOnSuccessRiderLocation(
                state
            )
            is TrackFoodOrderViewState.OnFaiFetchRiderLocationTrackFoodOrder -> renderOnFailRiderLocation(
                state
            )
            else -> {}
        }
    }

    override fun onPause() {
        super.onPause()
        updateWidgetHandler.removeCallbacks(updateWidgetRunnable)
        //binding.mapBoxMap.onPause()

    }

    override fun onDestroy() {
        updateWidgetHandler.removeCallbacks(updateWidgetRunnable)
        super.onDestroy()
        //binding.mapBoxMap.onDestroy()
    }

    private fun renderOnLoadingTrackFoodOrder() {}

    private fun renderOnSuccessTrackFoodOrder(state: TrackFoodOrderViewState.OnSuccessTrackFoodOrder) {
        if (state.data.success) viewModel.trackOrderLiveData.postValue(state.data.data)

    }

    private fun renderOnFailTrackFoodOrder(state: TrackFoodOrderViewState.OnFailTrackFoodOrder) {
        when (state.message) {
            "Connection Issue" -> showSnackBar(resources.getString(R.string.no_internet_title))

            "Another Login" -> {
                WarningDialog.Builder(this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        val intent = Intent(this, SplashActivity::class.java)
                        startActivity(intent)

                        //startActivity<SplashActivity>()
                    })
                    .show(supportFragmentManager, TrackOrderMapBoxActivity::class.simpleName)
            }

            "DENIED" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                })
                .show(supportFragmentManager, TrackOrderMapBoxActivity::class.simpleName)

            else -> showSnackBar(state.message)

        }
    }

    private fun renderOnSuccessRiderLocation(state: TrackFoodOrderViewState.OnSuccessFetchRiderLocation) {
        if (state.data.success) viewModel.rider_latlng =
            LatLng(state.data.data.rider_latitude, state.data.data.rider_longitude)
    }

    private fun renderOnFailRiderLocation(state: TrackFoodOrderViewState.OnFaiFetchRiderLocationTrackFoodOrder) {
        when (state.message) {
            "Connection Issue" -> showSnackBar(resources.getString(R.string.no_internet_title))

            "Another Login" ->
                WarningDialog.Builder(this,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        val intent = Intent(this, SplashActivity::class.java)
                        startActivity(intent)

                        //startActivity<SplashActivity>()
                    })
                    .show(supportFragmentManager, TrackOrderMapBoxActivity::class.simpleName)
            "DENIED" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                })
                .show(supportFragmentManager, TrackOrderMapBoxActivity::class.simpleName)
            else -> showSnackBar(state.message)

        }
    }

    private fun onBackPress() {
        binding.btnClose.setOnClickListener {
            //startActivity<MainActivity>()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /*override fun onMapReady(mapboxMap: MapboxMap) {
        mapBox = mapboxMap
        mapBox.uiSettings.isAttributionEnabled = false
        mapBox.uiSettings.isLogoEnabled = false
        mapBox.uiSettings.isCompassEnabled = false
        mapBox.locationComponent.isLocationComponentActivated
        mapBox.removeAnnotations()

        mapBox.setStyle(
            Style.MAPBOX_STREETS
        ) { style ->
            enableLocationComponent(style)
            mapBox.addMarker(
                MarkerOptions().setPosition(
                    com.mapbox.mapboxsdk.geometry.LatLng(
                        viewModel.customer_latlng.latitude, viewModel.customer_latlng.longitude
                    )
                ).setIcon(
                    IconFactory.getInstance(this).fromBitmap(
                        AppCompatResources.getDrawable(
                            this,
                            R.drawable.ic_your_location
                        )!!.toBitmap()
                    )
                )
            )

            mapBox.addMarker(
                MarkerOptions().setPosition(
                    com.mapbox.mapboxsdk.geometry.LatLng(
                        viewModel.restaurant_latlng.latitude,
                        viewModel.restaurant_latlng.longitude
                    )
                ).setIcon(
                    IconFactory.getInstance(this).fromBitmap(
                        AppCompatResources.getDrawable(
                            this,
                            R.drawable.ic_restaurant_pin
                        )!!.toBitmap()
                    )
                )
            )

            mapBox.addMarker(
                MarkerOptions().setPosition(
                    com.mapbox.mapboxsdk.geometry.LatLng(
                        viewModel.rider_latlng.latitude, viewModel.rider_latlng.longitude
                    )
                ).setIcon(
                    IconFactory.getInstance(this).fromBitmap(
                        AppCompatResources.getDrawable(
                            this,
                            R.drawable.ic_rider_pin
                        )!!.toBitmap()
                    )
                )
            )

            if (viewModel.rider_latlng != LatLng(0.0, 0.0)) {
                val latLngBounds = LatLngBounds.Builder()
                    .include(
                        com.mapbox.mapboxsdk.geometry.LatLng(
                            viewModel.customer_latlng.latitude,
                            viewModel.customer_latlng.longitude
                        )
                    )

                    .include(
                        com.mapbox.mapboxsdk.geometry.LatLng(
                            viewModel.restaurant_latlng.latitude,
                            viewModel.restaurant_latlng.longitude
                        )
                    )

                    .include(
                        com.mapbox.mapboxsdk.geometry.LatLng(
                            viewModel.rider_latlng.latitude,
                            viewModel.rider_latlng.longitude
                        )
                    )
                    .build()
                mapBox.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150))

            } else {
                val latLngBounds = LatLngBounds.Builder()
                    .include(
                        com.mapbox.mapboxsdk.geometry.LatLng(
                            viewModel.customer_latlng.latitude,
                            viewModel.customer_latlng.longitude
                        )
                    )
                    .include(
                        com.mapbox.mapboxsdk.geometry.LatLng(
                            viewModel.restaurant_latlng.latitude,
                            viewModel.restaurant_latlng.longitude
                        )
                    )
                    .build()
                mapBox.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150))
            }
        }
    }*/

    @SuppressLint("MissingPermission")
    /*private fun enableLocationComponent(loadedMapStyle: Style) {
        val customLocationComponentOptions = LocationComponentOptions.builder(this)
            .accuracyAlpha(0f)
            .elevation(0f)
            .pulseAlpha(0f)
            .pulseEnabled(false)
            .trackingGesturesManagement(true)
            .accuracyColor(ContextCompat.getColor(this, R.color.white))
            .build()

        val locationComponentActivationOptions =
            LocationComponentActivationOptions.builder(this, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

        mapBox.locationComponent.apply {

            // Activate the LocationComponent with options
            activateLocationComponent(locationComponentActivationOptions)

            // Enable to make the LocationComponent visible
            isLocationComponentEnabled = true

            // Set the LocationComponent's camera mode
            cameraMode = CameraMode.TRACKING

            // Set the LocationComponent's render mode
            renderMode = RenderMode.NORMAL
        }

    }*/

    override fun onStop() {
        super.onStop()
        //binding.mapBoxMap.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //binding.mapBoxMap.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        //binding.mapBoxMap.onLowMemory()
    }

}