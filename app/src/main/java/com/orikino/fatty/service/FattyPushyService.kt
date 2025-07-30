package com.orikino.fatty.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.orikino.fatty.R
import com.orikino.fatty.ui.views.activities.base.MainActivity
import com.orikino.fatty.ui.views.activities.delivery_rating.DeliveryRatingActivity
import com.orikino.fatty.ui.views.activities.order.OrderDetailActivity
import com.orikino.fatty.ui.views.activities.parcel.ParcelDetailActivity
import com.orikino.fatty.utils.PreferenceUtils
import me.pushy.sdk.Pushy

class FattyPushyService : Service() {

    private var orderTypeIntent: Intent? = null
    private var ratingFinishedChecking: Boolean = false
    private var idForFoodRating: Int = 0

    override fun onCreate() {}

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        PreferenceUtils.refreshOrderList.postValue(intent.getStringExtra("order_type"))
        val orderType = intent.getStringExtra("order_type")
        val type = intent.getStringExtra("type")
        val orderId = intent.getIntExtra("order_id", 0)
        val orderStatusId = intent.getIntExtra("order_status_id", 0)
        Log.d("Type #####", type.toString())
        Log.d("Type Status Id #####", orderStatusId.toString())
        if (orderId != 0) {
            idForFoodRating = orderId
        }

        when (orderType) {
            "food" -> {
                orderTypeIntent = when (type) {
                    "new_order" -> {
                        //order detail
                        PreferenceUtils.acceptOrder.postValue(true)
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false

                        if (checkService() == 0) {
                            val intent = Intent(this,OrderDetailActivity::class.java)
                            intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
                            intent
                        } else {
                            // map view
                            //val intent = Intent(this, TrackOrderMapBoxActivity::class.java)
                            val intent = Intent(this,OrderDetailActivity::class.java)
                            intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
                            intent
                        }
                    }

                    "restaurant_accept_order"  -> {
                        //order detail
                        PreferenceUtils.acceptOrder.postValue(true)
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false

                        if (checkService() == 0) {
                            val intent = Intent(this,OrderDetailActivity::class.java)
                            intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
                            intent
                        } else {
                            val intent = Intent(this,OrderDetailActivity::class.java)
                            intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
                            intent
                        }
                    }

                    "rider_accept_order" -> {
                        PreferenceUtils.acceptOrder.postValue(true)
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false

                        if (checkService() == 0) {
                            val intent = Intent(this,OrderDetailActivity::class.java)
                            intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
                            intent
                        } else {
                            val intent = Intent(this,OrderDetailActivity::class.java)
                            intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
                            intent
                        }
                    }

                    "ready_pickup_order" -> {
                        PreferenceUtils.acceptOrder.postValue(true)
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false
                        val intent = Intent(this,OrderDetailActivity::class.java)
                        intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
                        intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
                        intent
                        if (checkService() == 0) {
                            val intent = Intent(this,OrderDetailActivity::class.java)
                            intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
                            intent
                        } else {
                            val intent = Intent(this,OrderDetailActivity::class.java)
                            intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
                            intent
                        }
                    }

                    "rider_arrived" -> {
                        PreferenceUtils.acceptOrder.postValue(true)
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false
                        val intent = Intent(this,OrderDetailActivity::class.java)
                        intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
                        intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
                        intent
                        if (checkService() == 0) {
                            val intent = Intent(this,OrderDetailActivity::class.java)
                            intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
                            intent
                        } else {
                            val intent = Intent(this,OrderDetailActivity::class.java)
                            intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
                            intent
                        }
                    }

                    "rider_start_delivery" -> {
                        PreferenceUtils.acceptOrder.postValue(true)
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false
                        val intent = Intent(this,OrderDetailActivity::class.java)
                        intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
                        intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
                        intent
                        if (checkService() == 0) {
                            val intent = Intent(this,OrderDetailActivity::class.java)
                            intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
                            intent
                        } else {
                            val intent = Intent(this,OrderDetailActivity::class.java)
                            intent.putExtra(OrderDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(OrderDetailActivity.INTENT_FROM,true)
                            intent
                        }
                    }

                    "restaurant_cancel_order" -> {
                        PreferenceUtils.acceptOrder.postValue(true)
                        MainActivity.isOrderHistory = true
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false
                        MainActivity.getIntent(this)
                    }

                    "restaurant_each_order_cancel" -> {
                        PreferenceUtils.acceptOrder.postValue(true)
                        MainActivity.isOrderHistory = true
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false
                        MainActivity.getIntent(this)
                    }

                    "rider_order_finished" -> {
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false
                        ratingFinishedChecking = true
                        DeliveryRatingActivity.getIntent(this, idForFoodRating, orderType)
                    }

                    else -> Intent().apply {
                        this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                }
            } else -> {
                orderTypeIntent = when (type) {
                    "new_order" -> {
                        PreferenceUtils.acceptOrder.postValue(true)
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false

                        if (checkService() == 0) {
                            val intent = Intent(this,ParcelDetailActivity::class.java)
                            intent.putExtra(ParcelDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(ParcelDetailActivity.PARCEL_HISTORY,true)
                            intent
                        } else {
                            val intent = Intent(this,ParcelDetailActivity::class.java)
                            intent.putExtra(ParcelDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(ParcelDetailActivity.PARCEL_HISTORY,true)
                            intent
                        }
                    }

                    "rider_accept_parcel_order" -> {
                        PreferenceUtils.acceptOrder.postValue(true)
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false

                        if (checkService() == 0) {
                            val intent = Intent(this,ParcelDetailActivity::class.java)
                            intent.putExtra(ParcelDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(ParcelDetailActivity.PARCEL_HISTORY,true)
                            intent
                        } else {
                            val intent = Intent(this,ParcelDetailActivity::class.java)
                            intent.putExtra(ParcelDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(ParcelDetailActivity.PARCEL_HISTORY,true)
                            intent
                        }
                    }

                    "rider_arrived_pickup_address" -> {
                        PreferenceUtils.acceptOrder.postValue(true)
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false
                        val intent = Intent(this,ParcelDetailActivity::class.java)
                        intent.putExtra(ParcelDetailActivity.ORDER_ID,orderId)
                        intent.putExtra(ParcelDetailActivity.PARCEL_HISTORY,true)
                        intent
                        if (checkService() == 0) {
                            val intent = Intent(this,ParcelDetailActivity::class.java)
                            intent.putExtra(ParcelDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(ParcelDetailActivity.PARCEL_HISTORY,true)
                            intent
                        } else {
                            val intent = Intent(this,ParcelDetailActivity::class.java)
                            intent.putExtra(ParcelDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(ParcelDetailActivity.PARCEL_HISTORY,true)
                            intent
                        }
                    }

                    "rider_start_delivery_parcel" -> {
                        PreferenceUtils.acceptOrder.postValue(true)
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false
                        val intent = Intent(this,ParcelDetailActivity::class.java)
                        intent.putExtra(ParcelDetailActivity.ORDER_ID,orderId)
                        intent.putExtra(ParcelDetailActivity.PARCEL_HISTORY,true)
                        intent
                        if (checkService() == 0) {
                            val intent = Intent(this,ParcelDetailActivity::class.java)
                            intent.putExtra(ParcelDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(ParcelDetailActivity.PARCEL_HISTORY,true)
                            intent
                        } else {
                            val intent = Intent(this,ParcelDetailActivity::class.java)
                            intent.putExtra(ParcelDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(ParcelDetailActivity.PARCEL_HISTORY,true)
                            intent
                        }
                    }

                    "rider_parcel_update" -> {
                        PreferenceUtils.acceptOrder.postValue(true)
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false
                        val intent = Intent(this,ParcelDetailActivity::class.java)
                        intent.putExtra(ParcelDetailActivity.ORDER_ID,orderId)
                        intent.putExtra(ParcelDetailActivity.PARCEL_HISTORY,true)
                        intent
                        if (checkService() == 0) {
                            val intent = Intent(this,ParcelDetailActivity::class.java)
                            intent.putExtra(ParcelDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(ParcelDetailActivity.PARCEL_HISTORY,true)
                            intent
                        } else {
                            val intent = Intent(this,ParcelDetailActivity::class.java)
                            intent.putExtra(ParcelDetailActivity.ORDER_ID,orderId)
                            intent.putExtra(ParcelDetailActivity.PARCEL_HISTORY,true)
                            intent
                        }
                    }

                    "rider_parcel_cancel_order" -> {
                        MainActivity.isParcel = true
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false
                        MainActivity.getIntent(this)
                    }

                    "rider_parcel_order_finished" -> {
                        PreferenceUtils.acceptOrder.postValue(true)
                        PreferenceUtils.needToShow = false
                        PreferenceUtils.isBackground = false
                        ratingFinishedChecking = true
                        DeliveryRatingActivity.getIntent(this, idForFoodRating, orderType)
                    }

                    else -> Intent().apply {
                        this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                }
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            orderTypeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "10044"
        val NOTIFICATION_CHANNEL_NAME = "fatty food new"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setUpNotificationChannels(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                notificationManager
            )
        }

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.fatty)
                // .setLargeIcon(resolveUriAsBitmap(Uri.parse("android.resource://com.fatty.fooddelivery/" + R.drawable.fatty)))
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("body"))
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(longArrayOf(0, 400, 250, 400))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // Automatically configure a Notification Channel for devices running Android O+
        Pushy.setNotificationChannel(builder, this)
        notificationManager.notify(1001, builder.build())
        startForeground(1, builder.notification)

        if (ratingFinishedChecking ) {
            orderTypeIntent?.flags =  Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(orderTypeIntent)
        }
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpNotificationChannels(
        channelId: String,
        channelName: String,
        notificationManager: NotificationManager
    ) {
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.enableLights(true)
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
        channel.lightColor = Color.GREEN
        channel.enableVibration(true)
        notificationManager.createNotificationChannel(channel)
    }

    private fun checkService(): Int {
        var finalResult = -1
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(applicationContext)

        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                finalResult = result
            }
        } else {
            finalResult = if (result == 0) 0
            else result
        }
        return finalResult
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {}
}
