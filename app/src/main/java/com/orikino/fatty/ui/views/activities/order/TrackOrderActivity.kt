package com.orikino.fatty.ui.views.activities.order

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ActivityTrackOrderBinding
import com.orikino.fatty.domain.model.ActiveOrderVO
import com.orikino.fatty.service.FattyPushyService
import com.orikino.fatty.domain.view_model.TrackOrderViewModel
import com.orikino.fatty.domain.viewstates.TrackOrderViewState
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.helper.showSnackBar

class TrackOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackOrderBinding

    var location: Location = Location("")
    private val updateWidgetHandler = Handler()
    //private lateinit var fattyMap: FattyMap
    private var orderId: Int = 0
    private var riderId: Int = 0
    private var isUpdateRider = false
    private var total = 0.0
    private var billTotal = 0.0
    private var fooAddOnList = mutableListOf<String>()
    private lateinit var sheetBehavior: BottomSheetBehavior<*>
    private lateinit var bottom_sheet: ConstraintLayout
    private val viewModel: TrackOrderViewModel by viewModels()

    companion object {
        const val ORDER_ID = "order_id"
        const val IS_UPDATE_RIDER = "is-update-rider"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrackOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(ORDER_ID)) orderId = intent.getIntExtra(ORDER_ID, 0)
        isUpdateRider = intent.getBooleanExtra(IS_UPDATE_RIDER, false)

        stopService()
    }

    private fun stopService() {
        val intent = Intent(this, FattyPushyService::class.java)
        stopService(intent)
    }


    override fun onResume() {
        super.onResume()
        subscribeUI()
        //updateWidgetHandler.postDelayed(updateWidgetRunnable, UPDATE_INTERVAL)
        //setUpMap()
        //checkGPS()

    }

    private fun render(state : TrackOrderViewState) {
        when (state) {
            is TrackOrderViewState.OnLoadingTrackFoodOrder -> renderOnLoadingTrackFoodOrder()
            is TrackOrderViewState.OnSuccessTrackFoodOrder -> renderOnSuccessTrackFoodOrder(state)
            is TrackOrderViewState.OnFailTrackFoodOrder -> renderOnFailTrackFoodOrder(state)
            //is OrderViewState.OnSuccessRiderLocation -> renderOnSuccessRiderLocation(state)
            //is OrderViewState.OnFailRiderLocation -> renderOnFailRiderLocation(state)

            else -> {}
        }
    }

    private fun renderOnLoadingTrackFoodOrder(){}

    private fun renderOnSuccessTrackFoodOrder(state : TrackOrderViewState.OnSuccessTrackFoodOrder) {
        if (state.data.success) {
            //viewModel.trackOrderLiveData.postValue(state.data.data)
        }
    }
    private fun renderOnFailTrackFoodOrder(state: TrackOrderViewState.OnFailTrackFoodOrder) {
        showSnackBar(state.message)
    }

    private fun subscribeUI() {
        //viewModel.trackFood(orderId)
        viewModel.viewState.observe(this, { render(it) })
        /*viewModel.trackOrderLiveData.observe(this) {
            bindOrderInfo(it)
            bindOrderBills(it)
        }*/
    }

    private fun bindOrderInfo(data : ActiveOrderVO) {
        binding.tvOrderEstimatedDate.text = data.order_time
        binding.tvOrderEstimatedDate.text =
            if (data.estimated_start_time != null && data.estimated_end_time != null) "${data.estimated_start_time} - ${data.estimated_end_time}" else "02:45PM - 03:15PM"
        binding.tvOrderId.text =
            "${resources.getString(R.string.order_id)} | ${data.customer_order_id}"

        binding.tvReceiverAddress.text = data.customer?.customer_phone

        when (data.order_status?.order_status_id) {
            1 -> binding.tvOrderStatusMsg.text = data.order_status?.order_status_name
            3 -> binding.tvOrderStatusMsg.text = data.order_status?.order_status_name
            4 -> {
                /*bind.setColorFilter(
                    ContextCompat.getColor(this, R.color.fatty),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                tv_food_status.text = data.order_status.order_status_name*/

            }

            5 -> {
/*                imv_motor.setColorFilter(
                    ContextCompat.getColor(this, R.color.fatty),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                progress_rider_show.visibility = View.VISIBLE
                tv_food_status.text = data.order_status.order_status_name*/
            }


            6 -> {
                /*imv_motor.setColorFilter(
                    ContextCompat.getColor(this, R.color.fatty),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                progress_rider_show.visibility = View.VISIBLE
                tv_food_status.text = data.order_status.order_status_name*/
            }
            7 -> {
                /*imv_user.setColorFilter(
                    ContextCompat.getColor(this, R.color.fatty),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                imv_motor.setColorFilter(
                    ContextCompat.getColor(this, R.color.fatty),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                progress_rider_show.visibility = View.VISIBLE
                tv_food_status.text = data.order_status.order_status_name*/
            }
            10 -> {
                /*imv_motor.setColorFilter(
                    ContextCompat.getColor(this, R.color.fatty),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                progress_rider_show.visibility = View.VISIBLE
                tv_food_status.text = data.order_status.order_status_name*/
            }
        }

        /*geoCodeLocationToAddress(
            data.customer_address_latitude ?: 0.0,
            data.customer_address_longitude ?: 0.0
        )
        setUpOrderItemRecycler(data.foods, data.currency_id)
        tv_payment_name.text = data.payment_method.payment_method_name
        tv_delivery_fee.text =
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
            rider.visibility = View.VISIBLE
            callToRider(data.rider?.rider_user_phone ?: "")
            //imv_rider_photo.loadSlide(if (BuildConfig.DEBUG) "${Preference.DEVELOPMENT_URL}uploads/rider/${data.rider?.rider_image}" else "${Preference.PRODUCTION_URL}uploads/rider/${data.rider?.rider_image}")
            imv_rider_photo.loadSlide(Preference.IMAGE_URL.plus("/rider/").plus(data.rider?.rider_image))
            tv_rider_name.text = data.rider?.rider_user_name
            viewModel.rider_latlng =
                LatLng(data.rider!!.rider_latitude, data.rider!!.rider_longitude)

        }

        tv_total_original_value.text = "${
            total.plus(data.delivery_fee).toThousandSeparator()
        } ${if (data.currency_id == 1) "MMK" else "¥"}"
        tv_total_price.text =
            "${data.bill_total_price.toThousandSeparator()} ${if (data.currency_id == 1) "MMK" else "¥"}"

        viewModel.customer_latlng =
            LatLng(data.customer_address_latitude ?: 0.0, data.customer_address_longitude ?: 0.0)
        viewModel.restaurant_latlng = LatLng(
            data.restaurant?.restaurant_latitude ?: 0.0,
            data.restaurant?.restaurant_longitude ?: 0.0
        )

        setUpMap()

        imvSendMessage.setOnClickListener {
            startActivity<ChattingActivity>(
                ChattingActivity.RIDER_ID to data.rider!!.rider_id,
                ChattingActivity.ORDER to Gson().toJson(data)
            )
        }*/
    }

    private fun bindOrderBills(it : ActiveOrderVO) {

    }


    private fun renderOnSuccessRiderLocation() {}
    private fun renderOnFailRiderLocation() {}

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}