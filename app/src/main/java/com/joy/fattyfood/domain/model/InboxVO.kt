package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class InboxVO(
    @SerializedName("order_id")
    var order_id : Int = 0,
    @SerializedName("status_title")
    var status_title : String = "",
    @SerializedName("restaurant_name")
    var restaurant_name : String ="",
    @SerializedName("cancel_amount")
    var cancel_amount : Double = 0.0,
    @SerializedName("customer_order_id")
    var customer_order_id : String ?=null,
    @SerializedName("date")
    var date : String ="",
    @SerializedName("time")
    var time : String ="",
    @SerializedName("noti_menu_id")
    var noti_menu_id : Int =0,
    @SerializedName("noti_menu")
    var noti_menu : String ?=null,
    @SerializedName("notification_title")
    var notification_title : String ?=null,
    @SerializedName("notification_body")
    var notification_body : String ?=null,
    @SerializedName("notification_image")
    var notification_image : String ?=null,
    @SerializedName("currency_id")
    var currency_id : Int = 0
)
