package com.orikino.fatty.ui.views.activities.parcel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.orikino.fatty.R
import com.orikino.fatty.databinding.ActivityParcelDetailBinding
import com.orikino.fatty.domain.model.ActiveOrderVO
import com.orikino.fatty.domain.model.OrderStatusVO
import com.orikino.fatty.domain.model.RatingDetailVO
import com.orikino.fatty.domain.view_model.TrackOrderViewModel
import com.orikino.fatty.domain.viewstates.OrderDetailWithRatingViewState
import com.orikino.fatty.domain.viewstates.TrackParcelViewState
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.utils.GpsTracker
import com.orikino.fatty.utils.LoadingProgressDialog
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import com.orikino.fatty.utils.helper.toHourMinuteString
import com.orikino.fatty.utils.helper.toThousandSeparator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParcelDetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityParcelDetailBinding


    private var order_id : String? = ""

    companion object {
        const val ORDER_ID = "order_id"
        const val PARCEL_HISTORY = "parcel_history"
    }

    private var parcelHistoryVO = ""
    private var data = ActiveOrderVO()

    private val viewModel : TrackOrderViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParcelDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        order_id = intent.getIntExtra(ORDER_ID,0).toString()
        setUpSubScribe()
        onBackPress()



    }



    private fun onBackPress() {
        binding.ivBack.setOnClickListener { finish() }
    }


    private fun setUpSubScribe() {
        //order_id?.let { viewModel.trackParcelOrder(it) }
        order_id?.let { viewModel.orderDetailWithRating(it) }
        viewModel.viewStateOrderDetail.observe(this) {render(it)}
    }

    private fun render(state : OrderDetailWithRatingViewState) {
        when(state) {
            is OrderDetailWithRatingViewState.OnLoadingOrderDetailDetailWithRating -> renderOnLoadingTrackParcelOrder()
            is OrderDetailWithRatingViewState.OnSuccessOrderDetailWithRating -> renderOnSuccessOrderDetailWithRating(state)
            is OrderDetailWithRatingViewState.OnFailOrderDetailWithRating -> renderOnFailOrderDetailWithRating(state)

        }
    }


    private fun renderOnSuccessOrderDetailWithRating(state: OrderDetailWithRatingViewState.OnSuccessOrderDetailWithRating) {
        LoadingProgressDialog.hideLoadingProgress()
        if (state.data.success == true) {
           state.data.data?.let { bindCheckParcelInfo(it) }
        }

    }

    private fun renderOnFailOrderDetailWithRating(state: OrderDetailWithRatingViewState.OnFailOrderDetailWithRating) {

    }

    private fun renderOnLoadingTrackParcelOrder() {
        LoadingProgressDialog.showLoadingProgress(this@ParcelDetailActivity)
    }


    @SuppressLint("SetTextI18n")
    private fun bindCheckParcelInfo(data : ActiveOrderVO) {
        data.order_status?.let { localizeOrderStatus(it) }
        binding.tvOrderId.text = "${getString(R.string.order_id)} ${data.customer_order_id}"
        binding.tvParcelTypeName.text = data.parcel_type?.parcel_type_name
        binding.tvParcelPrice.text= data.item_total_price?.toThousandSeparator().plus(PreferenceUtils.readCurrCurrency()?.currency_symbol)
        binding.tvParcelQtyCount.text = data.item_qty.toString()
        binding.tvOrderStatus.text = data.order_status?.order_status_name
        binding.tvParcelEstimatedDate.text = data.order_date.plus(" | ").plus(data.order_time)
           // (data.estimated_start_time)?.let { dateFormat(it).plus(" | ").plus(data.order_time) }
        binding.tvDeliverDurationDistance.text = ( data.distance_time?.toHourMinuteString()).plus("/").plus(data.distance.toString().plus(" km"))
        binding.tvOrderFrom.text = getString(R.string.sender)
        binding.tvSenderName.text = data.rider?.rider_user_name
        binding.tvSenderAddress.text = data.from_parcel_city_name
        binding.tvSenderPhone.text = data.rider?.rider_user_phone

        binding.tvReceiverName.text = data.from_sender_name
        binding.tvReceiverAddress.text = data.to_parcel_city_name
        binding.tvReceiverPhone.text  = data.from_sender_phone


        /*if (data.parcel_extra != null) {
            tv_extra_cover.visibility = View.VISIBLE
            tv_extra_cover_cost.text = "${data.parcel_extra?.parcel_extra_cover_price?.toThousandSeparator()} ${if(data.currency_id == 1) "MMK" else "¥"  }"
            tv_extra_cover_price.text = "${data.parcel_extra?.parcel_extra_cover_price?.toThousandSeparator()} ${if(data.currency_id == 1) "MMK" else "¥"  }"
            //imv_extra_cover.loadSlide(if (BuildConfig.DEBUG) "${Preference.DEVELOPMENT_URL}uploads/parcel/parcel_extra_cover/${data.parcel_extra?.parcel_extra_cover_image}" else "${Preference.PRODUCTION_URL}uploads/parcel/parcel_extra_cover/${data.parcel_extra?.parcel_extra_cover_image}")
            imv_extra_cover.loadSlide(Preference.IMAGE_URL.plus("/parcel/parcel_extra_cover/").plus(data.parcel_extra?.parcel_extra_cover_image))
        } else {
            tv_extra_cover.visibility = View.GONE
            tv_extra_cover_price.visibility = View.GONE
            imv_extra_cover.visibility = View.GONE
            tv_extra_cover_cost.text = "0.0 ${if(data?.currency_id == 1) "MMK" else "¥"  }"
        }*/

        /*if (data.have_review == 0) {
            binding.riderRatingView.gone()
        } else {
            binding.riderRatingView.show()
        }

        */
        binding.tvTotalItemPrice.text = data.item_qty.toString()
        if (data.is_abnormal == true){
            binding.tvDeliveryFeeTitle.text = getString(R.string.delivery_fees_abnormal)
        }else{
            binding.tvDeliveryFeeTitle.text = getString(R.string.delivery_fees)
        }

        binding.tvDeliveryFeePrice.text = data.delivery_fee.toThousandSeparator().plus(PreferenceUtils.readCurrCurrency()?.currency_symbol)
        //binding.tvExtraFeePrice.text = data.parcel_extra?.parcel_extra_cover_price?.toThousandSeparator().plus(PreferenceUtils.readCurrCurrency()?.currency_symbol)
        binding.tvBillTotalPrice.text = data.bill_total_price.toThousandSeparator().plus(PreferenceUtils.readCurrCurrency()?.currency_symbol)

        if (data.additional_fee != 0.0 || data.additional_fee_yuan != 0.0){
            binding.rlAdditional.show()
            if (data.currency_id == 1){
                binding.tvAdditionalFee.text = data.additional_fee.toThousandSeparator().plus(PreferenceUtils.readCurrCurrency()?.currency_symbol)
            }else{
                binding.tvAdditionalFee.text = data.additional_fee_yuan.toThousandSeparator().plus(PreferenceUtils.readCurrCurrency()?.currency_symbol)
            }
        }else{
            binding.rlAdditional.gone()
        }

        if (data.parcel_extra?.parcel_extra_cover_price != 0.0){
            binding.rlExtra.show()
            binding.tvExtraFell.text = data.parcel_extra?.parcel_extra_cover_price?.toThousandSeparator().plus(PreferenceUtils.readCurrCurrency()?.currency_symbol)
        }


        if (data.payment_method?.payment_method_id == 1) {
            binding.imvPayment.setImageResource(R.drawable.ic_cash)
            binding.tvPaymentName.text = getString(R.string.cash_on_delivery)
        } else {
            binding.imvPayment.setImageResource(R.drawable.kbz_payment)
            binding.tvPaymentName.text = data.payment_method?.payment_method_name
        }

        if (data.rider != null) {

        }

        if (data.rider_review != null) {
            binding.rlNoRating.gone()
            binding.rlHaveRating.show()
            data.rider_review?.let { bindRating(it) }
        } else {
            binding.rlNoRating.gone()
            binding.rlHaveRating.gone()
        }

        /*when (orderStatus) {
            "parcel" -> {
                binding.tvOrderId.text = "${"Order ID "}: $orderId"
                binding.tvOrderStatus.text = data.parcel_type?.parcel_type_name//resources.getString(R.string.booked_order_successfully)
                MainActivity.isParcel = true
                PreferenceUtils.needToShow = false
            }
            else -> {
                MainActivity.isParcel = false
                binding.tvOrderStatus.text = data.parcel_type?.parcel_type_name//resources.getString(R.string.booked_order_successfully)
                if (isKPay) try {
                    KBZPay.startPay(this@ParcelDetailActivity, orderInfo, sing, singType)
                } catch (e: Exception) {
                    showSnackBar("Kpay Error Found")
                    MainActivity.isOrderHistory = true
                    PreferenceUtils.needToShow = false
                    finish()
                    startActivity<MainActivity>()

                }
            }
        }*/
    }

    fun convertPhoneNumber(input: String) {
        val formattedNumber = input.removePrefix("+95").removePrefix("959")
        binding.tvReceiverPhone.text = formattedNumber
    }
    private fun bindRating(data : RatingDetailVO) {
        binding.txtRiderParcelCmt.text = data.comment
        binding.ratingBarRiderParcel.rating = data.star!!.toFloat()
        // TODO
    }

    private fun localizeOrderStatus(type : OrderStatusVO) {
        when (type.order_status_id) {
            11 -> {
                binding.tvOrderStatus.text = type.order_status_name
                binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_processing_20dp)
            }
            12 -> {
                binding.tvOrderStatus.text = type.order_status_name
                binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_success_20dp)
            }
            15 -> {
                binding.tvOrderStatus.text = type.order_status_name
                binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_success_20dp)
            }
            18 -> {
                binding.tvOrderStatus.text = type.order_status_name//resources.getString(R.string.kpay_pending)
                binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_processing_20dp)
            }

            19 -> {
                binding.tvOrderStatus.text = type.order_status_name//resources.getString(R.string.kpay_success)
                binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_success_20dp)
            }

            20 -> {
                binding.tvOrderStatus.text = type.order_status_name//resources.getString(R.string.kpay_fail)
                binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_status_error_20dp)
            }

        }
    }
    private fun renderOnFailTrackParcelOrder(state: TrackParcelViewState.OnFailTrackParcelOrder) {
        LoadingProgressDialog.hideLoadingProgress()
        when (state.message) {

            "Server Issue" -> {
                showSnackBar(state.message)
            }

            "Another Login" -> {
                WarningDialog.Builder(this@ParcelDetailActivity,
                    resources.getString(R.string.already_login_title),
                    resources.getString(R.string.already_login_msg),
                    resources.getString(R.string.force_login),
                    callback = {
                        PreferenceUtils.clearCache()
                        finish()
                        startActivity(LoginActivity.getIntent("wish_list"))
                    }).show(supportFragmentManager, ParcelDetailActivity::class.simpleName)
            }

            "Denied" -> WarningDialog.Builder(this@ParcelDetailActivity,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()

                }).show(supportFragmentManager, ParcelDetailActivity::class.simpleName)

            else -> showSnackBar(state.message!!)
        }
    }


    override fun onResume() {
        super.onResume()
        checkGPS()

    }

    private fun checkGPS() {
        var gpsTracker = GpsTracker(this)
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert()
        } else {
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }

}