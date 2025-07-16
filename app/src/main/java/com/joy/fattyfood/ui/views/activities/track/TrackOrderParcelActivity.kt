package com.joy.fattyfood.ui.views.activities.track

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import coil.load
import com.kbzbank.payment.KBZPay
import com.joy.fattyfood.R
import com.joy.fattyfood.databinding.ActivityTrackOrderParcelBinding
import com.joy.fattyfood.domain.model.ActiveOrderVO
import com.joy.fattyfood.domain.view_model.TrackOrderViewModel
import com.joy.fattyfood.domain.viewstates.OrderDetailWithRatingViewState
import com.joy.fattyfood.service.FattyPushyService
import com.joy.fattyfood.ui.views.activities.base.MainActivity
import com.joy.fattyfood.ui.views.activities.delivery_rating.DeliveryRatingActivity
import com.joy.fattyfood.utils.GpsTracker
import com.joy.fattyfood.utils.LoadingProgressDialog
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.WarningDialog
import com.joy.fattyfood.utils.helper.gone
import com.joy.fattyfood.utils.helper.phoneCall
import com.joy.fattyfood.utils.helper.show
import com.joy.fattyfood.utils.helper.showSnackBar
import com.joy.fattyfood.utils.helper.toThousandSeparator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackOrderParcelActivity : AppCompatActivity() {


    private lateinit var _binding : ActivityTrackOrderParcelBinding

    private val viewModel : TrackOrderViewModel by viewModels()


    private var orderId: String = ""
    private var isKPay = false
    private var orderInfo = ""
    private var sing = ""
    private var singType = ""
    private var orderStatus: String = ""
    private var lastSelected = 0
    private var isEdit = false
    private var isParcelType = false
    private var isBack = true
    private var viewType = ""
    private var orderStatusId = 0
    private var customerOrderId = ""
    private var rider_ph = ""

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
        const val VIEW_TYPE = "view_type"
        const val ORDER_STATUS_ID = "order_status_id"
        const val CUSTOMER_ORDER_ID = "customer_order_id"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        _binding = ActivityTrackOrderParcelBinding.inflate(layoutInflater)
        setContentView(_binding.root)


        orderId = intent.getIntExtra(ORDER_ID,0).toString()
        orderStatus = intent.getStringExtra(ORDER_STATUS).toString()
        isKPay = intent.getBooleanExtra(IS_KPAY, false)
        orderInfo = intent.getStringExtra(ORDER_INFO).toString()
        sing = intent.getStringExtra(SIGN).toString()
        singType = intent.getStringExtra(SIGN_TYPE).toString()

        viewType = intent.getStringExtra(VIEW_TYPE).toString()
        orderStatusId = intent.getIntExtra(ORDER_STATUS_ID,0)
        customerOrderId = intent.getStringExtra(CUSTOMER_ORDER_ID).toString()


        MainActivity.isOrderHistory = true
        PreferenceUtils.needToShow = false

        setUpSubScribe()
        //checkOrder(ActiveOrderVO())
        stopService()
        navigateToTrackOrderView()
        callToRider("")

        if (viewType == "his" && orderId != "" ) {
            viewModel.orderDetailWithRating(orderId)

        }


    }


    private fun setUpSubScribe() {
        viewModel.viewStateOrderDetail.observe(this) { render(it) }
    }

    private fun render(state : OrderDetailWithRatingViewState) {
        when(state) {
            is OrderDetailWithRatingViewState.OnLoadingOrderDetailDetailWithRating -> renderOnLoadingOrderDetailDetailWithRating()
            is OrderDetailWithRatingViewState.OnSuccessOrderDetailWithRating -> renderOnSuccessOrderDetailWithRating(state)
            is OrderDetailWithRatingViewState.OnFailOrderDetailWithRating -> renderOnFailOrderDetailWithRating(state)

        }
    }

    private fun renderOnLoadingOrderDetailDetailWithRating() {}

    private fun renderOnSuccessOrderDetailWithRating(state: OrderDetailWithRatingViewState.OnSuccessOrderDetailWithRating){
        if (state.data.success == true) {
            state.data.data?.let { showTrackParcelStateView(it) }


            //state.data.data?.let { bindSenderReceiver(it) }
            _binding.tvSenderName.text = state.data.data?.rider?.rider_user_name
            _binding.tvSenderAddress.text = state.data.data?.from_parcel_city_name
            _binding.tvSenderPhone.text = state.data.data?.from_sender_phone

            _binding.tvReceiverName.text = state.data.data?.customer?.customer_name
            _binding.tvReceiverAddress.text = state.data.data?.to_parcel_city_name
            _binding.tvReceiverPhone.text = state.data.data?.customer?.customer_phone

            state.data.data?.let { bindParcelSpecific(it) }
            state.data.data?.let { bindBillDetails(it) }
            state.data.data?.let { setUpBindRiderInfo(it) }
            if (state.data.data?.rider?.rider_user_phone != null) {
                state.data.data.rider?.rider_user_phone?.let { callToRider(it) }
            }
            if(state.data.data?.rider_review != null) {
                _binding.llRating.show()
            } else {
                _binding.llRating.gone()
            }
        }
    }

    private fun bindParcelSpecific(data: ActiveOrderVO) {
        _binding.tvParcelTypeName.text = data.parcel_type?.parcel_type_name
        _binding.tvParcelQtyCount.text = data.item_qty.toString()
    }

    private fun bindBillDetails(data: ActiveOrderVO) {
        _binding.tvTotalAmount.text = data.bill_total_price.toThousandSeparator().plus(if (data.currency_id == 1) "MMK" else "¥")
        _binding.tvDeliveryFeePrice.text = data.delivery_fee.toThousandSeparator().plus(if (data.currency_id == 1) "MMK" else "¥")
    }

    private fun callToRider(riderPhone : String) {
        _binding.ivCall.setOnClickListener {
            if (riderPhone != "") {
                phoneCall(riderPhone)
            }

        }
    }

    private fun showTrackParcelStateView(data: ActiveOrderVO) {
        when(data.order_status?.order_status_id) {
            12 -> {
                // pending order
                _binding.tvOrderStatus.show()
                _binding.ivOrderStatusPng.setImageResource(R.drawable.order_pending)
                _binding.tvOrderStatusTitle.text = resources.getString(R.string.pending_order)
                _binding.tvOrderStatus.text = resources.getString(R.string.pending_order)
                _binding.tvCusOrderId.text = data.customer_booking_id
            }
            14 -> {
                _binding.tvOrderStatus.show()
                _binding.ivOrderStatusPng.setImageResource(R.drawable.order_pickup)
                _binding.tvOrderStatusTitle.text = resources.getString(R.string.pick_up)
                _binding.tvOrderStatus.text = resources.getString(R.string.pick_up)
                _binding.tvCusOrderId.text = data.customer_booking_id
                _binding.tvTrackOrderDate.text = data.rider_accept_time
            }
            15 -> {
                _binding.ivOrderStatusPng.setImageResource(R.drawable.group_success_order)
                _binding.tvOrderStatusTitle.text = resources.getString(R.string.order_complete)
                _binding.tvOrderStatus.gone()
                _binding.tvCusOrderId.text = data.customer_booking_id
                _binding.tvTrackOrderDate.text = data.rider_accept_time
            }
            else -> {
                _binding.tvOrderStatus.show()
                _binding.ivOrderStatusPng.setImageResource(R.drawable.order_food_pending_cook)
                _binding.tvOrderStatusTitle.text = resources.getString(R.string.pending_order)
                _binding.tvOrderStatus.text = resources.getString(R.string.pending_order)
                _binding.tvCusOrderId.text = data.customer_order_id
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun checkOrder(data: ActiveOrderVO) {
        /*"new_order"
        "rider_accept_parcel_order"
        "rider_arrived_pickup_address"
        "rider_start_delivery_parcel"
        "rider_parcel_update"
        "rider_parcel_cancel_order"
        "rider_parcel_order_finished"*/
        when (orderStatus) {
            "new_order" -> {
                _binding.llRating.gone()
                _binding.tvOrderStatusTitle.text = resources.getString(R.string.pending_order)
                _binding.tvOrderStatus.text = resources.getString(R.string.booked_order_successfully)
                _binding.ivOrderStatusPng.load(R.drawable.order_pending)
                MainActivity.isParcel = true
                PreferenceUtils.needToShow = false
            }
            "rider_accept_parcel_order" -> {
                _binding.llRating.gone()
                _binding.tvOrderStatusTitle.text = resources.getString(R.string.pending_order)
                _binding.tvOrderStatus.text = resources.getString(R.string.booked_order_successfully)
                _binding.ivOrderStatusPng.load(R.drawable.order_pending)
                MainActivity.isParcel = true
                PreferenceUtils.needToShow = false
            }
            "rider_arrived_pickup_address" -> {
                _binding.llRating.show()
                _binding.tvOrderStatusTitle.text = resources.getString(R.string.pick_up)
                _binding.tvOrderStatus.text = resources.getString(R.string.pick_up)
                _binding.ivOrderStatusPng.load(R.drawable.order_pickup)
                MainActivity.isParcel = true
                PreferenceUtils.needToShow = false
            }
            "rider_start_delivery_parcel" -> {
                _binding.llRating.show()
                _binding.tvOrderStatusTitle.text = resources.getString(R.string.on_the_way)
                _binding.tvOrderStatus.text = resources.getString(R.string.on_the_way)
                _binding.ivOrderStatusPng.load(R.drawable.order_on_way)
                MainActivity.isParcel = true
                PreferenceUtils.needToShow = false
            }
            "rider_parcel_update" -> {
                _binding.llRating.show()
                _binding.tvOrderStatusTitle.text = "rider_parcel_update"//resources.getString(R.string.on_the_way)
                _binding.tvOrderStatus.text = "rider_parcel_update"//resources.getString(R.string.on_the_way)
                _binding.ivOrderStatusPng.load(R.drawable.order_on_way)
                MainActivity.isParcel = true
                PreferenceUtils.needToShow = false
            }
            "rider_parcel_cancel_order" -> {
                _binding.llRating.show()
                _binding.tvOrderStatusTitle.text = "rider_parcel_cancel_order"//resources.getString(R.string.on_the_way)
                _binding.tvOrderStatus.text = "rider_parcel_cancel_order"//resources.getString(R.string.on_the_way)
                _binding.ivOrderStatusPng.load(R.drawable.order_on_way)
                MainActivity.isParcel = true
                PreferenceUtils.needToShow = false
            }

            else -> {
                _binding.llRating.show()
                _binding.tvOrderStatusTitle.text = resources.getString(R.string.order_complete)
                _binding.tvOrderStatus.gone()
                _binding.ivOrderStatusPng.load(R.drawable.group_success_order)
                //rateOurService()
                MainActivity.isParcel = false
                if (isKPay) try {
                    KBZPay.startPay(this@TrackOrderParcelActivity, orderInfo, sing, singType)
                } catch (e: Exception) {
                    showSnackBar("Kpay Error Found")
                    MainActivity.isOrderHistory = true
                    PreferenceUtils.needToShow = false
                    finish()
                    //startActivity<MainActivity>()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                }


            }
        }
    }
    override fun onResume() {
        super.onResume()
        checkGPS()

    }

    private fun startRateRider() {
        Handler(Looper.getMainLooper()).postDelayed({
            //rateOurService()
            //startActivity<DeliveryRatingActivity>()
            val intent = Intent(this, DeliveryRatingActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)

    }

    private fun checkGPS() {
        var gpsTracker = GpsTracker(this)
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert()
        } else {
        }
    }





    private fun renderOnFailOrderDetailWithRating(state: OrderDetailWithRatingViewState.OnFailOrderDetailWithRating) {}


    private fun bindSenderReceiver(data: ActiveOrderVO) {
        _binding.tvSenderName.text = data.rider?.rider_user_name
        _binding.tvSenderAddress.text = data.from_parcel_city_name
        _binding.tvSenderPhone.text = data.from_sender_phone

        _binding.tvReceiverName.text = data.customer?.customer_name
        _binding.tvReceiverAddress.text = data.to_parcel_city_name
        _binding.tvReceiverPhone.text = data.customer_address_phone
        println("datatatattt $data")
    }

    private fun setUpBindRiderInfo(data: ActiveOrderVO) {
        _binding.tvRiderName.text = data.rider?.rider_user_name
        _binding.imvRider.load(PreferenceUtils.IMAGE_URL.plus("/rider/").plus(data.rider?.rider_image)) {
            error(R.drawable.fatty_rounded)
            placeholder(R.drawable.fatty_rounded)
        }

    }

    private fun setUpBindParcelInfo(data : ActiveOrderVO){
        /*_binding.tvOrderStatus.text = data.order_status?.order_status_name
        _binding.tvCusOrderId.text = data.order_id.toString()
        _binding.tvOrderDate.text = data.order_time*/
        _binding.tvParcelTypeName.text = data.parcel_type?.parcel_type_name
        _binding.tvParcelQtyCount.text = data.item_qty.toString()
        //_binding.tvDeliveryFee.text = data.delivery_fee.toThousandSeparator().plus(PreferenceUtils.readCurrCurrency()?.currency_symbol)

        if (data.parcel_extra != null) {
            _binding.rlExtraView.show()
            _binding.tvExtraCost.text = data.parcel_extra?.parcel_extra_cover_price?.toThousandSeparator().plus(PreferenceUtils.readCurrCurrency()?.currency_symbol)
        } else {
            _binding.rlExtraView.gone()
        }

        _binding.tvTotalAmount.text = data.bill_total_price.toThousandSeparator().plus(PreferenceUtils.readCurrCurrency()?.currency_symbol)
        _binding.tvPaymentName.text = data.payment_method?.payment_method_name
        if (data.payment_method?.payment_method_id == 1) {
            _binding.imvPayment.setImageResource(R.drawable.ic_cash)

        } else {
            _binding.imvPayment.setImageResource(R.drawable.kbz_payment)
        }

    }
    private fun renderOnLoadingTrackParcelOrder() {
        LoadingProgressDialog.showLoadingProgress(this@TrackOrderParcelActivity)
    }


    private fun navigateToTrackOrderView() {
        /*btnTrackOrder.setOnClickListener {
            BaseActivity.isOrderHistory = true
            Preference.needToShow = false
            startActivity<BaseActivity>()
            finish()
        }*/
    }


    private fun pickUp() {
        _binding.tvOrderStatusTitle.text = resources.getString(R.string.pick_up)
        _binding.ivOrderStatusPng.setImageResource(R.drawable.order_pickup)
        _binding.tvOrderStatus.text = resources.getString(R.string.pick_up)
        Handler(Looper.getMainLooper()).postDelayed({
            //onTheWay()
        }, 4000)
    }


    private fun onTheWay() {
        _binding.tvOrderStatusTitle.text = resources.getString(R.string.on_the_way)
        _binding.ivOrderStatusPng.setImageResource(R.drawable.order_on_way)
        _binding.tvOrderStatus.text = resources.getString(R.string.on_the_way)
        Handler(Looper.getMainLooper()).postDelayed({
            //successOrder()
        }, 4000)
    }


    private fun successOrder() {
        _binding.tvOrderStatusTitle.text = resources.getString(R.string.order_complete)
        _binding.ivOrderStatusPng.setImageResource(R.drawable.group_success_order)
        _binding.tvOrderStatus.gone()

        Handler(Looper.getMainLooper()).postDelayed({
            //rateOurService()
        }, 4000)

    }


    private fun rateOurService() {
        WarningDialog.Builder(this,
            resources.getString(R.string.success_delivery_title),
            resources.getString(R.string.success_delivery_desc),
            resources.getString(R.string.rate_our),
            callback = {

                startActivity(DeliveryRatingActivity.getIntent(this,0,""))
            })
            .show(supportFragmentManager, TrackOrderParcelActivity::class.java.simpleName)
    }

    private fun stopService() {
        val intent = Intent(this, FattyPushyService::class.java)
        stopService(intent)
    }

}