package com.orikino.fatty.ui.views.activities.checkout

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kbzbank.payment.KBZPay
import com.kbzbank.payment.KBZPay.startPay
import com.orikino.fatty.R
import com.orikino.fatty.app.FattyApp
import com.orikino.fatty.databinding.ActivityPlaceOrderBinding
import com.orikino.fatty.ui.views.activities.base.MainActivity
import com.orikino.fatty.utils.LocaleHelper
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.showSnackBar

class PlaceOrderActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPlaceOrderBinding

    private var orderId: String? = "0"
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

        orderId = intent.getStringExtra(ORDER_ID)
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
                binding.tvOrderId.text = resources.getString(R.string.order_id).plus(" ").plus(orderId)
                binding.tvOrderStatus.text = resources.getString(R.string.order_success)
                MainActivity.isParcel = true
                PreferenceUtils.needToShow = false
            }
            else -> {
                MainActivity.isParcel = false
                binding.tvOrderId.text = resources.getString(R.string.order_id).plus(" ").plus(orderId)
                if (isKPay) try {
                    startPay(this@PlaceOrderActivity, orderInfo, sing, singType)
                } catch (e: Exception) {
                    showSnackBar("Kpay Error Found")
                    MainActivity.isOrderHistory = true
                    PreferenceUtils.needToShow = false
                    finishAffinity()
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

    override fun attachBaseContext(newBase: Context?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            super.attachBaseContext(LocaleHelper().onAttach(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}