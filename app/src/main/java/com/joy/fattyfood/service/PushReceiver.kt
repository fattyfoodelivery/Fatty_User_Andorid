package com.joy.fattyfood.service

import android.content.Intent
import android.content.Context
import android.content.BroadcastReceiver
import android.os.Build
import android.util.Log
import com.joy.fattyfood.utils.PreferenceUtils

class PushReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var notificationTitle = ""
        var notificationText = ""
        when(PreferenceUtils.readLanguage()){
            "en" ->{
                notificationTitle = intent?.getStringExtra("title_en").toString()
                notificationText = intent?.getStringExtra("body_en").toString()
            }
            "zh" ->{
                notificationTitle = intent?.getStringExtra("title_ch").toString()
                notificationText = intent?.getStringExtra("body_ch").toString()
            }
            else ->{
                notificationTitle = intent?.getStringExtra("title_mm").toString()
                notificationText = intent?.getStringExtra("body_mm").toString()
            }
        }

        // Prepare a notification with vibration, sound and lights
        val intent1 = Intent(context, FattyPushyService::class.java)



        intent1.putExtra("title", notificationTitle)
        intent1.putExtra("body", notificationText)
        intent1.putExtra("order_type",intent?.getStringExtra("order_type").toString())
        intent1.putExtra("type",intent?.getStringExtra("type").toString())
        intent1.putExtra("order_status_id",intent?.getIntExtra("order_status_id",0))
        intent1.putExtra("order_id",intent?.getIntExtra("order_id",0))

        Log.d("OrderId #### receiver ", intent?.getIntExtra("order_id", 0).toString())
        Log.d("OrderStatusId #### receiver ", intent?.getIntExtra("order_status_id", 0).toString())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.applicationContext?.startForegroundService(intent1)
        } else {
            context?.startService(intent1)
        }
    }
}