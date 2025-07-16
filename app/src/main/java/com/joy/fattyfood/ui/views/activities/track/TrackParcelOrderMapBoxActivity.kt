package com.joy.fattyfood.ui.views.activities.track

import android.annotation.SuppressLint
import android.content.Intent
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
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.ActivityTrackParcelOrderMapBoxBinding
import com.joy.fattyfood.databinding.TrackParcelOrderBottomsheetBinding
import com.joy.fattyfood.domain.model.ActiveOrderVO
import com.joy.fattyfood.domain.view_model.TrackParcelOrderViewModel
import com.joy.fattyfood.domain.viewstates.TrackParcelOrderViewState
import com.joy.fattyfood.service.FattyPushyService
import com.joy.fattyfood.ui.views.activities.account_setting.help_center.HelpCenterActivity
import com.joy.fattyfood.ui.views.activities.base.MainActivity
import com.joy.fattyfood.ui.views.activities.chat.ChattingActivity
import com.joy.fattyfood.ui.views.activities.splash.SplashActivity
import com.joy.fattyfood.utils.GpsTracker
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.WarningDialog
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.phoneCall
import com.joy.fattyfood.utils.helper.show
import com.joy.fattyfood.utils.helper.showSnackBar
import com.joy.fattyfood.utils.helper.toThousandSeparator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TrackParcelOrderMapBoxActivity : AppCompatActivity()/* , OnMapReadyCallback*/ {

    private lateinit var binding : ActivityTrackParcelOrderMapBoxBinding

    lateinit var track_parcel_order_bottom_sheet : TrackParcelOrderBottomsheetBinding

    companion object {
        const val ORDER_ID = "order_id"
        const val IS_UPDATE_RIDER = "is-update-rider"
    }

    lateinit var sheetBehavior: BottomSheetBehavior<*>
    lateinit var bottom_sheet: ConstraintLayout
    private var orderId = 0
    private var riderId: Int = 0
    //private lateinit var mapBox: MapboxMap

    private val viewModel: TrackParcelOrderViewModel by viewModels()


    private val updateWidgetHandler = Handler()
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*Mapbox.getInstance(
            this@TrackParcelOrderMapBoxActivity,
            "sk.eyJ1IjoiZmF0dHlmb29kIiwiYSI6ImNsMTdqaXl6eDA0aGszZ280YzF1MXNjMHoifQ.d5Qvf_pXSsxX7nLjgVArFw"
        )*/
        binding = ActivityTrackParcelOrderMapBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //binding.mapBoxMap.onCreate(savedInstanceState)
        //binding.mapBoxMap.getMapAsync(this)

        //binding.root = findViewById(R.id.track_parcel_order_bottom_sheet)
        //track_parcel_order_bottom_sheet = BottomSheetBehavior.from(this)
        sheetBehavior = BottomSheetBehavior.from(track_parcel_order_bottom_sheet.root)
        if (intent.hasExtra(ORDER_ID)) {
            orderId = intent.getIntExtra(ORDER_ID, 0)
        }
        setUpParcelPicturesRecyclerView()
        setUpBottomSheet()
        contactToCallCenter()
        stopService()
        onBackPress()
    }

    private fun stopService() {
        val intent = Intent(this, FattyPushyService::class.java)
        stopService(intent)
    }

    private fun setUpBottomSheet() {
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        track_parcel_order_bottom_sheet.llHeaderView.gone()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        track_parcel_order_bottom_sheet.llHeaderView.show()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        track_parcel_order_bottom_sheet.tbTPBack.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun callToRider(phone: String) {
        track_parcel_order_bottom_sheet.imvPhone.setOnClickListener {
            phoneCall(phone)
        }
    }

    private fun contactToCallCenter() {
        binding.btnAgent.setOnClickListener {
            val intent = Intent(this, HelpCenterActivity::class.java)
            startActivity(intent)
            //startActivity<HelpCenterActivity>()
        }
    }

    private fun subscribeUI() {
        viewModel.trackParcelOrder(orderId)
        viewModel.viewState.observe(this, { render(it) })
        viewModel.trackParcelOrderLiveData.observe(this) {
            bindOrderInfo(it)
        }
    }

    private fun render(state: TrackParcelOrderViewState) {
        when (state) {
            is TrackParcelOrderViewState.OnLoadingTrackParcelOrder -> renderOnLoadingTrackParcelOrder()
            is TrackParcelOrderViewState.OnSuccessTrackParcelOrder -> renderOnSuccessTrackParcelOrder(
                state
            )
            is TrackParcelOrderViewState.OnFailTrackParcelOrder -> renderOnFailTrackParcelOrder(
                state
            )
            is TrackParcelOrderViewState.OnSuccessFetchRiderLocation -> renderOnSuccessRiderLocation(
                state
            )
            is TrackParcelOrderViewState.OnFaiFetchRiderLocation -> renderOnFailRiderLocation(
                state
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindOrderInfo(data: ActiveOrderVO) {

        track_parcel_order_bottom_sheet.tvEstimateTimeValue.text =
            if (data.estimated_start_time != null && data.estimated_end_time != null) "${data.estimated_start_time} - ${data.estimated_end_time}" else "02:45PM - 03:15PM"

        binding.tvOrderTime.text = data.order_time
        binding.tvParcelOrderId.text = "Order ID | ${data.customer_order_id}"
        track_parcel_order_bottom_sheet.tvOrderIdValue.text = data.customer_order_id
        track_parcel_order_bottom_sheet.tvBookingIdValue.text = data.customer_booking_id
        track_parcel_order_bottom_sheet.tvParcelStatus.text = data.order_status?.order_status_name

        when (data.order_status?.order_status_id) {
            11 -> track_parcel_order_bottom_sheet.tvParcelStatus.text = data.order_status?.order_status_name
            12 -> track_parcel_order_bottom_sheet.tvParcelStatus.text = data.order_status?.order_status_name
            13 -> {
                track_parcel_order_bottom_sheet.imvPickupPlace.setColorFilter(
                    ContextCompat.getColor(this, R.color.fattyPrimary),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )

                track_parcel_order_bottom_sheet.tvParcelStatus.text = data.order_status?.order_status_name

            }
            17 -> {
                track_parcel_order_bottom_sheet.imvPickupPlace.setColorFilter(
                    ContextCompat.getColor(this, R.color.fattyPrimary),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                //rider_progress_bar_show.visibility = View.VISIBLE
                track_parcel_order_bottom_sheet.tvParcelStatus.text = data.order_status?.order_status_name
            }
            14 -> {
                track_parcel_order_bottom_sheet.imvPickupPlace.setColorFilter(
                    ContextCompat.getColor(this, R.color.fattyPrimary),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                //rider_progress_bar_show.visibility = View.VISIBLE
                track_parcel_order_bottom_sheet.tvParcelStatus.text = data.order_status?.order_status_name
            }
            15 -> {
                track_parcel_order_bottom_sheet.imvDestination.setColorFilter(
                    ContextCompat.getColor(this, R.color.fattyPrimary),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                track_parcel_order_bottom_sheet.imvPickupPlace.setColorFilter(
                    ContextCompat.getColor(this, R.color.fattyPrimary),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                //rider_progress_bar_show.visibility = View.VISIBLE
                track_parcel_order_bottom_sheet.tvParcelStatus.text = data.order_status?.order_status_name
            }
        }

        track_parcel_order_bottom_sheet.tvSenderName.text = data.from_parcel_city_name
        track_parcel_order_bottom_sheet.tvSenderPhone.text = data.from_sender_phone
        track_parcel_order_bottom_sheet.tvSenderAddress.text = data.from_pickup_address
        track_parcel_order_bottom_sheet.tvReceiverName.text = data.to_parcel_city_name
        track_parcel_order_bottom_sheet.tvReceiverPhone.text = data.to_recipent_phone
        track_parcel_order_bottom_sheet.tvReceiverAddress.text = data.to_drop_address
        track_parcel_order_bottom_sheet.tvParcelTypeName.text = data.parcel_type?.parcel_type_name
        track_parcel_order_bottom_sheet.tvWeight.text = data.total_estimated_weight.toString()
        track_parcel_order_bottom_sheet.tvDeliFee.text =
            "${data.delivery_fee.toThousandSeparator()} ${if (data.currency_id == 1) "MMK" else "¥"}"


        if (data.from_pickup_address != null) track_parcel_order_bottom_sheet.tvSenderAddress.text = data.from_pickup_address
        else track_parcel_order_bottom_sheet.tvSenderAddress.show()

        if (data.to_drop_address != null) track_parcel_order_bottom_sheet.tvReceiverAddress.text = data.to_drop_address
        else track_parcel_order_bottom_sheet.tvReceiverAddress.gone()

        if (data.from_pickup_note != null) track_parcel_order_bottom_sheet.tvSenderNote.text = data.from_pickup_note
        else track_parcel_order_bottom_sheet.tvSenderNote.gone()

        if (data.to_drop_note != null) track_parcel_order_bottom_sheet.tvReceiverNote.text = data.to_drop_note
        else track_parcel_order_bottom_sheet.tvReceiverNote.gone()



        track_parcel_order_bottom_sheet.tvTotalEstimatedPrice.text =
            "${data.bill_total_price.toThousandSeparator()} ${if (data.currency_id == 1) "MMK" else "¥"}"
        if (data.parcel_extra != null) {
            track_parcel_order_bottom_sheet.tvExtraCover.visibility = View.VISIBLE
            track_parcel_order_bottom_sheet.tvExtraCoverPrice.text =
                "${data.parcel_extra?.parcel_extra_cover_price?.toThousandSeparator()} ${if (data.currency_id == 1) "MMK" else "¥"}"
            track_parcel_order_bottom_sheet.tvExtraCoverPrice.text =
                "${data.parcel_extra?.parcel_extra_cover_price?.toThousandSeparator()} ${if (data.currency_id == 1) "MMK" else "¥"}"
            track_parcel_order_bottom_sheet.imvExtraCover.load(
                PreferenceUtils.IMAGE_URL.plus("/parcel/parcel_extra_cover/").plus(data.parcel_extra?.parcel_extra_cover_image)){
                error(R.drawable.ic_parcel_document_39dp)
                placeholder(R.drawable.ic_parcel_document_39dp)
            }
        } else {
            track_parcel_order_bottom_sheet.tvExtraCover.gone()
            track_parcel_order_bottom_sheet.tvExtraCoverPrice.gone()
            track_parcel_order_bottom_sheet.imvExtraCover.gone()
            track_parcel_order_bottom_sheet.tvExtraCoverCost.text = "0.0 ${if (data.currency_id == 1) "MMK" else "¥"}"
        }

        setUpParcelPicturesRecyclerView(data)

        viewModel.customer_latlng =
            LatLng(data.from_pickup_latitude ?: 0.0, data.from_pickup_longitude ?: 0.0)
        viewModel.destination_latlng =
            LatLng(data.to_drop_latitude ?: 0.0, data.to_drop_longitude ?: 0.0)

        //tv_address_status.text = data.customer_address?.address_type
        //tv_current_address.text = data.customer_address?.customer_address
        //setUpOrderItemRecycler(data.foods)
        //tv_payment_name.text = data.payment_method.payment_method_name
        //tv_delivery_fee.text = "${data.delivery_fee}"

        if (data.rider != null) {
            riderId = data.rider!!.rider_id
            viewModel.fetchRiderLocation(riderId)
            callToRider(data.rider?.rider_user_phone ?: "")
            track_parcel_order_bottom_sheet.rider.show()
            track_parcel_order_bottom_sheet.imvRiderPhoto.load(PreferenceUtils.IMAGE_URL.plus("/rider/").plus(data.rider?.rider_image)) {
                error(R.drawable.fatty_rounded)
                placeholder(R.drawable.fatty_rounded)
            }
            track_parcel_order_bottom_sheet.tvRiderName.text = data.rider?.rider_user_name
            viewModel.rider_latlng =
                LatLng(data.rider!!.rider_latitude, data.rider!!.rider_longitude)

        }

        //onMapReady(mapBox)

        track_parcel_order_bottom_sheet.imvMessage.setOnClickListener {
            /*startActivity<ChattingActivity>(
                ChattingActivity.RIDER_ID to data.rider!!.rider_id,
                ChattingActivity.ORDER to Gson().toJson(data)
            )*/
            val intent = Intent(this, ChattingActivity::class.java)
            intent.putExtra(ChattingActivity.RIDER_ID, data.rider!!.rider_id)
            intent.putExtra(ChattingActivity.ORDER, Gson().toJson(data))
            startActivity(intent)
        }
    }

    private fun setUpParcelPicturesRecyclerView(data: ActiveOrderVO) {
        /*rvParcelPictures.bind(
            data.parcel_images,
            R.layout.layout_items_add_pictures
        ) { parcelImage: ParcelImage, position: Int ->
            ivRemove.visibility = View.GONE
            imv_add_pics.loadSlide(if (BuildConfig.DEBUG) "${Preference.DEVELOPMENT_URL}uploads/parcel/parcel_image/${parcelImage.parcel_image}" else "${Preference.PRODUCTION_URL}uploads/parcel/parcel_image/${parcelImage.parcel_image}")
            imv_add_pics.setOnClickListener {
                startActivity<ZoomInZoomOutActivity>(
                    ZoomInZoomOutActivity.IMAGE to parcelImage.parcel_image,
                    ZoomInZoomOutActivity.IS_URI to false
                )
            }
        }.layoutManager(
            LinearLayoutManager(
                this@TrackParcelOrderMapBoxActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )*/
    }

    private fun renderOnLoadingTrackParcelOrder() {}
    private fun renderOnSuccessTrackParcelOrder(state: TrackParcelOrderViewState.OnSuccessTrackParcelOrder) {
        if (state.data.success) viewModel.trackParcelOrderLiveData.postValue(state.data.data)
    }

    private fun renderOnFailTrackParcelOrder(state: TrackParcelOrderViewState.OnFailTrackParcelOrder) {
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
                    .show(supportFragmentManager, TrackParcelOrderMapBoxActivity::class.simpleName)
            "DENIED" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                })
                .show(supportFragmentManager, TrackParcelOrderMapBoxActivity::class.simpleName)
            else -> showSnackBar(state.message)

        }
    }

    private fun renderOnSuccessRiderLocation(state: TrackParcelOrderViewState.OnSuccessFetchRiderLocation) {
        if (state.data.success) viewModel.rider_latlng =
            LatLng(state.data.data.rider_latitude, state.data.data.rider_longitude)
    }

    private fun renderOnFailRiderLocation(state: TrackParcelOrderViewState.OnFaiFetchRiderLocation) {
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
                    .show(supportFragmentManager, TrackParcelOrderMapBoxActivity::class.simpleName)

            "DENIED" -> WarningDialog.Builder(this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                })
                .show(supportFragmentManager, TrackParcelOrderMapBoxActivity::class.simpleName)

            else -> showSnackBar(state.message)

        }
    }


    private fun setUpParcelPicturesRecyclerView() {
        /*rvParcelPictures.bind(
            getAddPicList(),
            R.layout.layout_items_add_pictures
        ) { addPicturesVO: AddPicturesVO, position: Int ->
            imv_add_pics.setImageResource(addPicturesVO.picCover)
        }.layoutManager(
            LinearLayoutManager(
                this@TrackParcelOrderMapBoxActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )*/
    }

    private fun checkGPS() {
        var gpsTracker = GpsTracker(this)
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert()
        }
    }

    override fun onResume() {
        super.onResume()
        subscribeUI()
        updateWidgetHandler.postDelayed(updateWidgetRunnable, UPDATE_INTERVAL)
        //binding.mapBoxMap.onResume()
        checkGPS()
    }

    override fun onStop() {
        super.onStop()
        //binding.mapBoxMap.onStop()
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

    private fun onBackPress() {
        binding.btnParcelClose.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            //startActivity<MainActivity>()
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


        try {
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
                                R.drawable.ic_point_f
                            )!!.toBitmap()
                        )
                    )
                )

                mapBox.addMarker(
                    MarkerOptions().setPosition(
                        com.mapbox.mapboxsdk.geometry.LatLng(
                            viewModel.destination_latlng.latitude,
                            viewModel.destination_latlng.longitude
                        )
                    ).setIcon(
                        IconFactory.getInstance(this).fromBitmap(
                            AppCompatResources.getDrawable(
                                this,
                                R.drawable.ic_point_t
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

                if (viewModel.rider_latlng != LatLng(
                        0.0,
                        0.0
                    ) && viewModel.customer_latlng != LatLng(
                        0.0,
                        0.0
                    ) && viewModel.destination_latlng != LatLng(0.0, 0.0)
                ) {
                    val latLngBounds = LatLngBounds.Builder()
                        .include(
                            com.mapbox.mapboxsdk.geometry.LatLng(
                                viewModel.customer_latlng.latitude,
                                viewModel.customer_latlng.longitude
                            )
                        )
                        .include(
                            com.mapbox.mapboxsdk.geometry.LatLng(
                                viewModel.destination_latlng.latitude,
                                viewModel.destination_latlng.longitude
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
                                viewModel.rider_latlng.latitude,
                                viewModel.rider_latlng.longitude
                            )
                        )
                        .include(
                            com.mapbox.mapboxsdk.geometry.LatLng(
                                viewModel.destination_latlng.latitude,
                                viewModel.destination_latlng.longitude
                            )
                        )
                        .build()
                    mapBox.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150))
                }
            }
        } catch (e: Exception) {
        }


    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        val customLocationComponentOptions = LocationComponentOptions.builder(this)
            .foregroundDrawable(R.drawable.ic_baseline_circle_24)
            .bearingDrawable(R.drawable.ic_baseline_circle_24)
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

    override fun onStart() {
        super.onStart()
        //binding.mapBoxMap.onStart()
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