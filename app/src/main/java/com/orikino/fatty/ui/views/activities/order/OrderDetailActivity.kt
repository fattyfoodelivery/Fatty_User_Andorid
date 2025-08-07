package com.orikino.fatty.ui.views.activities.order

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.orikino.fatty.R
import com.orikino.fatty.adapters.OrderInfoAdapter
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityOrderDetailBinding
import com.orikino.fatty.domain.model.ActiveOrderVO
import com.orikino.fatty.domain.model.OrderStatusVO
import com.orikino.fatty.domain.model.RecommendRestaurantVO
import com.orikino.fatty.domain.view_model.TrackOrderViewModel
import com.orikino.fatty.domain.viewstates.OrderDetailWithRatingViewState
import com.orikino.fatty.ui.views.activities.auth.login.LoginActivity
import com.orikino.fatty.utils.Constants
import com.orikino.fatty.utils.EqualSpacingItemDecoration
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.WarningDialog
import com.orikino.fatty.utils.helper.gone
import com.orikino.fatty.utils.helper.show
import com.orikino.fatty.utils.helper.showSnackBar
import com.orikino.fatty.utils.helper.toDefaultRestaurantName
import com.orikino.fatty.utils.helper.toDefaultStatusName
import com.orikino.fatty.utils.helper.toThousandSeparator
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var orderInfoAdapter: OrderInfoAdapter
    private val viewModel: TrackOrderViewModel by viewModels()
    var orderID: String? = ""
    var fromHistory: Boolean = false
    companion object {

        const val ORDER_ID = "order_id"
        const val INTENT_FROM = "from-intent"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderID = intent.getIntExtra(ORDER_ID,0).toString()
        fromHistory = intent.getBooleanExtra(INTENT_FROM, false)

        if (fromHistory) {
            binding.tvToolTitle.text = resources.getString(R.string.order_summary)
            binding.llRestaurantContentOrder.gone()
        }
        setUpObserver()
        setUpFoodOrderList()
        onBack()
    }

    private fun onBack() {
        binding.ivBack.setOnClickListener { onBackPressed() }
    }

    private fun setUpObserver() {
        //orderID?.toInt()?.let { viewModel.trackFood(it) }
        orderID?.let { viewModel.orderDetailWithRating(it) }
        viewModel.viewStateOrderDetail.observe(this) { render(it) }
    }

    private fun render(state: OrderDetailWithRatingViewState) {
        when (state) {
            is OrderDetailWithRatingViewState.OnLoadingOrderDetailDetailWithRating -> renderOnLoadingOrderDetailDetailWithRating()
            is OrderDetailWithRatingViewState.OnSuccessOrderDetailWithRating -> renderOnSuccessOrderDetailWithRating(state)
            is OrderDetailWithRatingViewState.OnFailOrderDetailWithRating -> renderOnFailOrderDetailWithRating(state)
        }
    }

    private fun renderOnLoadingOrderDetailDetailWithRating() {
        binding.animLoading.show()
        binding.animLoading.isAnimating
    }

    private fun renderOnSuccessOrderDetailWithRating(state: OrderDetailWithRatingViewState.OnSuccessOrderDetailWithRating) {
        binding.animLoading.gone()
        if (state.data.success == true) {
            orderInfoAdapter.setCurrency(if (state.data.data?.currency_id == 1) "MMK" else "¥")
            state.data.data?.foods?.let { orderInfoAdapter.setNewData(it) }
            state.data.data?.restaurant?.let { bindRestaurantInfo(it) }
            state.data.data?.let { bindSendToReceiver(it) }
            state.data.data?.let { bindBillsInfo(it) }
            state.data.data?.let { bindOrderDetailInfo(it) }

        }
    }

    private fun setUpFoodOrderList() {
        val linearLayoutManager =
            LinearLayoutManager(FattyApp.getInstance(), LinearLayoutManager.VERTICAL, false)
        binding.rvFoodOrder.layoutManager = linearLayoutManager
        binding.rvFoodOrder.addItemDecoration(
            EqualSpacingItemDecoration(
                24,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding.rvFoodOrder.setHasFixedSize(true)
        binding.rvFoodOrder.isNestedScrollingEnabled = true
        orderInfoAdapter = OrderInfoAdapter(FattyApp.getInstance()) { data, str, pos ->
        }
        binding.rvFoodOrder.adapter = orderInfoAdapter
    }

    private fun bindOrderDetailInfo(data: ActiveOrderVO) {
        binding.tvOrderId.text = resources.getString(R.string.order_id_4).plus(" ").plus(data.customer_order_id)
        binding.tvOrderEstimatedDate.text = data.order_date.plus(" | ").plus(data.order_time)
        binding.tvDeliverDurationDistance.text = (data.order_time).plus("Min /").plus(data.rider_restaurant_distance.toString().plus("km"))
        data.order_status?.let { localizeOrderStatus(it) }
    }


    private fun dateFormat(createAt : String) : String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputDateFormat.parse(createAt)
        val formattedDate = outputDateFormat.format(date)
        return formattedDate
    }

    private fun bindRestaurantInfo(data: RecommendRestaurantVO) {
        binding.apply {
            Picasso.get()
                .load(PreferenceUtils.IMAGE_URL.plus("/restaurant/").plus(data.restaurant_image))
                .placeholder(R.drawable.restaurant_default_img)
                .error(R.drawable.restaurant_default_img)
                .into(binding.imvRestaurant)
            binding.tvRestaurantName.text = data.restaurant_name
            //binding.tvOrderPrice.text = "${dat} ${if (data.currency_id == 1) "MMK" else "¥"}"
            //binding.tvItemQty.text = "${foodOrderItemQty} ${if (data.item_qty?.equals(1) == true) "Item" else "Items"}"
        }
    }
    private fun bindSendToReceiver(data: ActiveOrderVO) {
        println("cccccc ${data.customer?.customer_name}")
        binding.tvSenderName.text = data.restaurant?.toDefaultRestaurantName()
        binding.tvReceiverName.text = PreferenceUtils.readUserVO().customer_name
        binding.tvReceiverAddress.text = data.current_address
        binding.tvReceiverPhone.text = data.customer_address_phone?.let { convertPhoneNumber(it) }
    }

    fun convertPhoneNumber(input: String): String {
        val formattedNumber = input.removePrefix("+")
        return formattedNumber
    }

    private fun bindBillsInfo(data: ActiveOrderVO) {
        if (data.currency_id == 1){
            binding.tvBillTotalPrice.text = "${data.bill_total_price?.toThousandSeparator()} MMK"
            binding.tvItemTotal.text = "${data.item_total_price?.toThousandSeparator()} MMK"
            binding.tvDeliveryFee.text =
                "${data.delivery_fee?.toThousandSeparator()} MMK"
        }else{
            binding.tvBillTotalPrice.text = "${data.yuan_price?.toThousandSeparator()} ¥"
            binding.tvItemTotal.text = "${data.item_total_price_yuan?.toThousandSeparator()} ¥"
            binding.tvDeliveryFee.text =
                "${data.delivery_fee_yuan?.toThousandSeparator()} ¥"
        }
        if (data.rider != null) {
            binding.rlAbnormal.show()
        } else {
            binding.rlAbnormal.gone()
        }
        binding.tvPaymentName.text = data.payment_method?.payment_method_name
        if (data.currency_id == 1) {
            if (data.payment_method?.payment_method_id == 1) {
                binding.imvPayment.setImageResource(R.drawable.ic_cash)
            } else {
                binding.imvPayment.setImageResource(R.drawable.kbz_payment)
            }
        }

    }

    private fun localizeOrderStatus(orderStatus: OrderStatusVO) {
        println("status id ${orderStatus.order_status_id}")
        when (orderStatus.order_status_id) {
            1 -> {
                binding.tvOrderStatusMsg.text = orderStatus.toDefaultStatusName()
            }
            2 -> {
                binding.tvOrderStatusMsg.text = orderStatus.toDefaultStatusName()
            }
            3 -> {
                binding.tvOrderStatusMsg.text = orderStatus.toDefaultStatusName()
            }
            4 -> {
                binding.tvOrderStatusMsg.text = orderStatus.toDefaultStatusName()
            }
            5 -> {
                binding.tvOrderStatusMsg.text = orderStatus.toDefaultStatusName()
            }
            6 -> {
                binding.tvOrderStatusMsg.text = orderStatus.toDefaultStatusName()
            }
            7 -> {
                binding.tvOrderStatusMsg.text = orderStatus.toDefaultStatusName()
            }
            8 -> {
                binding.tvOrderStatusMsg.text = orderStatus.toDefaultStatusName()
            }
            9 -> {
                binding.tvOrderStatusMsg.text = orderStatus.toDefaultStatusName()
                binding.tvOrderStatusMsg.setTextColor(ContextCompat.getColor(this,R.color.textError))
                binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_red_circle_close)
            }
            18 -> {
                binding.tvOrderStatusMsg.text = resources.getString(R.string.order_id_4)
                binding.tvOrderStatusMsg.setTextColor(ContextCompat.getColor(this,R.color.textError))
                binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_status_error_20dp)
            }
            19 -> {
                binding.tvOrderStatusMsg.text = resources.getString(R.string.kpay_success)
                binding.tvOrderStatusMsg.setTextColor(ContextCompat.getColor(this,R.color.success200))
                binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_success_20dp)
            }
            20 -> {
                binding.tvOrderStatusMsg.text = resources.getString(R.string.kpay_fail)
                binding.tvOrderStatusMsg.setTextColor(ContextCompat.getColor(this,R.color.textError))
                binding.ivOrderStatusIcon.setImageResource(R.drawable.ic_order_status_error_20dp)
            }
        }
    }

    private fun renderOnFailOrderDetailWithRating(state: OrderDetailWithRatingViewState.OnFailOrderDetailWithRating) {
        binding.animLoading.gone()
        when (state.message) {
            Constants.CONNECTION_ISSUE -> {
                showSnackBar(resources.getString(R.string.no_internet_title))
            }

            Constants.ANOTHER_LOGIN -> startActivity(LoginActivity.getIntent("track_order"))

            Constants.DENIED -> WarningDialog.Builder(
                this,
                resources.getString(R.string.maintain_title),
                resources.getString(R.string.maintain_msg),
                "OK",
                callback = {
                    finishAffinity()
                }
            )
                .show(supportFragmentManager, OrderDetailActivity::class.simpleName)

            Constants.FAILED -> { showSnackBar(state.message) }
            else -> {
                showSnackBar(state.message!!)
            }
        }
    }


    private fun timeConvertDiff(start : String?,end : String?) {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        var calendar = Calendar.getInstance()
        val sdate = dateFormat.parse(start)
        val edate = dateFormat.parse((end))
        calendar = Calendar.getInstance().apply {
            time = sdate
        }
        calendar = Calendar.getInstance().apply {
            time = edate
        }
        val startMillis =
            calendar.get(Calendar.HOUR_OF_DAY) * 3600000 +
                    calendar.get(Calendar.MINUTE) * 60000 +
                    calendar.get(Calendar.SECOND) * 1000
        val endMillis =
            calendar.get(Calendar.HOUR_OF_DAY) * 3600000 +
                    calendar.get(Calendar.MINUTE) * 60000 +
                    calendar.get(Calendar.SECOND) * 1000

        val timeDiff = startMillis - endMillis
    }
}
