package com.orikino.fatty.ui.views.activities.track

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ActivityTrackOrderFoodBinding
import com.orikino.fatty.domain.view_model.TrackOrderViewModel
import com.orikino.fatty.domain.viewstates.TrackOrderViewState
import com.orikino.fatty.service.FattyPushyService
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.load
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackOrderFoodActivity : AppCompatActivity() {


    private lateinit var binding : ActivityTrackOrderFoodBinding
    private val viewModel : TrackOrderViewModel by viewModels()

    private var orderId: Int = 0
    private var isKPay = false
    private var orderInfo = ""
    private var sing = ""
    private var singType = ""
    private var orderStatus: String = ""
    private var lastSelected = 0
    private var isEdit = false
    private var isParcelType = false
    private var isBack = true
    private var isUpdateRider = false

    companion object {

        const val ORDER_STATUS = "orderStatus"
        const val ORDER_ID = "order_id"
        const val IS_UPDATE_RIDER = "is_update_rider"
        const val IS_KPAY = "is-kpay"
        const val ORDER_INFO = "order-info"
        const val SIGN = "sign"
        const val SIGN_TYPE = "sign-type"

        const val IS_EDIT = "is-edit"
        const val IS_BACK = "is-back"
        const val SENDER_ADDRESS = "data"
        const val DEFAULT_ADDRESS = "default-address"
        const val AUTO_ADDRESS = "auto-address"
        const val IS_PARCEL_TYPE = "is-parcel-type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrackOrderFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(ORDER_ID)) orderId = intent.getIntExtra(ORDER_ID, 0)
        isUpdateRider = intent.getBooleanExtra(IS_UPDATE_RIDER, false)
        orderStatus = intent.getStringExtra(ORDER_STATUS).toString()
        checkFoodTrackStautus()

        setUpObserver()
        //contactToCallCenter()
        stopService()
        onBackPress()
        viewModel.trackParcel(orderId)
    }

    private fun onBackPress() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setUpObserver() {
        viewModel.viewState.observe(this) { render(it) }
    }



    private fun checkFoodTrackStautus() {
        when(orderStatus) {
            "new_order" -> {
                binding.rlRating.gone()
                binding.tvOrderId.text = orderId.toString()
                binding.tvOrderStatusTitle.text = ContextCompat.getString(this,R.string.pending_order)
                binding.tvOrderStatus.text = ContextCompat.getString(this,R.string.booked_order_successfully)
                binding.ivOrderStatusPng.load(R.drawable.order_food_pending)
            }
            "restaurant_accept_order" -> {
                binding.rlRating.gone()
                binding.tvOrderId.text = orderId.toString()
                binding.tvOrderStatusTitle.text = ContextCompat.getString(this,R.string.pending_order)
                binding.tvOrderStatus.text = ContextCompat.getString(this,R.string.booked_order_successfully)
                binding.ivOrderStatusPng.load(R.drawable.order_food_pending_cook)
            }
            "rider_accept_order" -> {
                binding.rlRating.gone()
                binding.tvOrderId.text = orderId.toString()
                binding.tvOrderStatusTitle.text = ContextCompat.getString(this,R.string.on_the_way)
                binding.tvOrderStatus.text = ContextCompat.getString(this,R.string.on_the_way)
                binding.ivOrderStatusPng.load(R.drawable.order_on_way)
            }
            "ready_pickup_order" -> {
                binding.rlRating.gone()
                binding.tvOrderId.text = orderId.toString()
                binding.tvOrderStatusTitle.text = ContextCompat.getString(this,R.string.pending_order)
                binding.tvOrderStatus.text = ContextCompat.getString(this,R.string.booked_order_successfully)
                binding.ivOrderStatusPng.load(R.drawable.order_food_pending)
            }
            "restaurant_cancel_order" -> {
                binding.rlRating.gone()
                binding.tvOrderId.text = orderId.toString()
                binding.tvOrderStatusTitle.text = ContextCompat.getString(this,R.string.pending_order)
                binding.tvOrderStatus.text = ContextCompat.getString(this,R.string.booked_order_successfully)
                binding.ivOrderStatusPng.load(R.drawable.order_food_pending)
            }
            "restaurant_each_order_cancel" -> {
                binding.rlRating.gone()
                binding.tvOrderId.text = orderId.toString()
                binding.tvOrderStatusTitle.text = ContextCompat.getString(this,R.string.pending_order)
                binding.tvOrderStatus.text = ContextCompat.getString(this,R.string.booked_order_successfully)
                binding.ivOrderStatusPng.load(R.drawable.order_food_pending)
            }
            "rider_order_finished" -> {
                binding.rlRating.gone()
                binding.tvOrderId.text = orderId.toString()
                binding.tvOrderStatusTitle.text = ContextCompat.getString(this,R.string.pending_order)
                binding.tvOrderStatus.text = ContextCompat.getString(this,R.string.booked_order_successfully)
                binding.ivOrderStatusPng.load(R.drawable.order_food_pending)
            }
            else -> {
                binding.rlRating.gone()
                binding.tvOrderId.text = orderId.toString()
                binding.tvOrderStatusTitle.text = ContextCompat.getString(this,R.string.pending_order)
                binding.tvOrderStatus.text = ContextCompat.getString(this,R.string.booked_order_successfully)
                binding.ivOrderStatusPng.load(R.drawable.order_food_pending)
            }

        }
    }



    private fun render(state : TrackOrderViewState) {
        when(state) {
            is TrackOrderViewState.OnLoadingTrackFoodOrder -> renderOnLoadingTrackFoodOrder()
            is TrackOrderViewState.OnSuccessTrackFoodOrder -> renderOnSuccessTrackFoodOrder(state)
            is TrackOrderViewState.OnFailTrackFoodOrder -> renderOnFailTrackFoodOrder(state)

            /*is TrackOrderViewState.OnLoadingRiderLocation -> renderOnLoadingRiderLocation()
            is TrackOrderViewState.OnSuccessRiderLocation -> renderOnSuccessRiderLocation(state)
            is TrackOrderViewState.OnFailRiderLocation -> renderOnFailRiderLocation(state)

            is TrackOrderViewState.OnLoadingCheckReview -> renderOnLoadingCheckReview()
            is TrackOrderViewState.OnSuccessCheckReviewOrder -> renderOnSuccessCheckReviewOrder(state)
            is TrackOrderViewState.OnFailCheckReviewOrder -> renderOnFailCheckReviewOrder(state)*/

            else -> {}
        }
    }

    private fun renderOnLoadingTrackFoodOrder() {}

    private fun renderOnSuccessTrackFoodOrder(state: TrackOrderViewState.OnSuccessTrackFoodOrder) {

    }

    private fun renderOnFailTrackFoodOrder(state : TrackOrderViewState.OnFailTrackFoodOrder) {

    }


    private fun stopService() {
        val intent = Intent(this, FattyPushyService::class.java)
        stopService(intent)
    }

}