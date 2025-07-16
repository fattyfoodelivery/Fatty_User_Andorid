package com.joy.sdk.callback

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kbzbank.payment.KBZPay
import com.joy.fattyfood.databinding.ActivityCallbackResultBinding

class CallbackResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCallbackResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCallbackResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val intent = intent
        val result = intent.getIntExtra(KBZPay.EXTRA_RESULT, 0)
        if (result == KBZPay.COMPLETED) {
            Log.d("KBZPay", "pay success!")
        } else {
            val failMsg = intent.getStringExtra(KBZPay.EXTRA_FAIL_MSG)
            Log.d("KBZPay", "pay fail, fail reason = $failMsg")
        }
    }
}