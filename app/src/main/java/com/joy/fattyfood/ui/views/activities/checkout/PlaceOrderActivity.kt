package com.joy.fattyfood.ui.views.activities.checkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kbzbank.payment.KBZPay
import com.joy.fattyfood.R
import com.joy.fattyfood.app.FattyApp
import com.joy.fattyfood.databinding.ActivityPlaceOrderBinding
import com.joy.fattyfood.ui.views.activities.base.MainActivity
import com.joy.fattyfood.utils.PreferenceUtils
import com.joy.fattyfood.utils.helper.showSnackBar

class PlaceOrderActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPlaceOrderBinding

    private var orderId: Int? = 0
    private var isKPay = false
    private var orderInfo = ""
    private var sing = ""
    private var singType = ""
    private var orderStatus: String = ""

    companion object {

        const val ORDER_ID = "order_id"
        const val orderStatus = "order_status"
        const val IS_KPAY = "is-kpay"
        const val ORDER_INFO = "order-info"
        const val SIGN = "sign"
        const val SIGN_TYPE = "sign-type"

        fun getIntent(customer_order_id : String,isKpay : Boolean,order_info : String,sign : String,sign_type : String) : Intent {
            val intent = Intent(FattyApp.getInstance(), PlaceOrderActivity::class.java)
            intent.putExtra(ORDER_ID,customer_order_id)
            intent.putExtra(IS_KPAY,isKpay)
            intent.putExtra(ORDER_INFO,order_info)
            intent.putExtra(SIGN,sign)
            intent.putExtra(SIGN_TYPE,sign_type)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlaceOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderId = intent.getIntExtra(ORDER_ID,0)
        orderStatus = intent.getStringExtra(Companion.orderStatus).toString()
        isKPay = intent.getBooleanExtra(IS_KPAY, false)
        orderInfo = intent.getStringExtra(ORDER_INFO).toString()
        sing = intent.getStringExtra(SIGN).toString()
        singType = intent.getStringExtra(SIGN_TYPE).toString()

        binding.tvOrderId.text = resources.getString(R.string.order_id).plus(" ").plus(orderId)
        MainActivity.isOrderHistory = true
        PreferenceUtils.needToShow = false
        checkOrder()
        navigateToTrackOrderView()


    }

    private fun checkOrder() {
        when (orderStatus) {
            "parcel" -> {
                binding.tvOrderId.text = "${resources.getString(R.string.order_id)} : $orderId"
                binding.tvOrderStatus.text = resources.getString(R.string.order_success)
                MainActivity.isParcel = true
                PreferenceUtils.needToShow = false
            }
            else -> {
                MainActivity.isParcel = false
                binding.tvOrderId.text = "${resources.getString(R.string.order_id)} : $orderId"
                if (isKPay) try {
                    KBZPay.startPay(this@PlaceOrderActivity, orderInfo, sing, singType)
                } catch (e: Exception) {
                    showSnackBar("Kpay Error Found")
                    MainActivity.isOrderHistory = true
                    PreferenceUtils.needToShow = false
                    finish()
                    startActivity(MainActivity.getIntent(this))

                }
            }
        }
    }

    private fun navigateToTrackOrderView() {
        binding.tvViewOrder.setOnClickListener {
            MainActivity.isOrderHistory = true
            PreferenceUtils.needToShow = false
            startActivity(MainActivity.getIntent(this))
            finish()
        }
    }

}