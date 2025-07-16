package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class NearByRestaurantVO(
    @SerializedName("merchant_ads_id")
    var merchant_ads_id : Int = 0,
    @SerializedName("display_type_id")
    var display_type_id : Int = 0,
    @SerializedName("display_type_image")
    var display_type_image : String = "",
    @SerializedName("display_type_description")
    var display_type_description : String = "",
    @SerializedName("image_mm")
    var image_mm : String = "",
    @SerializedName("image_en")
    var image_en : String = "",
    @SerializedName("image_ch")
    var image_ch : String = "",
    @SerializedName("ads_type_id")
    var ads_type_id: Int = 0,
    @SerializedName("image")
    var image: String = "",
    @SerializedName("rating")
    var rating : Double = 0.0,

    @SerializedName("listing_type")
    var listing_type : Int = 0,
    @SerializedName("restaurant_id")
    var restaurant_id : Int = 0,
    @SerializedName("restaurant_name")
    var restaurant_name : String = "" ,
    @SerializedName("restaurant_category_name")
    var restaurant_category_name : String = "",
    @SerializedName("restaurant_category_id")
    var restaurant_category_id : Int = 0,
    @SerializedName("is_wish")
    var is_wish : Boolean = false,
    @SerializedName("distance")
    var distance : Double = 0.0,
    @SerializedName("distance_time")
    var distance_time : Int = 0,
    @SerializedName("delivery_fee")
    var delivery_fee : Double = 0.0,
    @SerializedName("define_amount")
    var define_amount : Double = 0.0,
    @SerializedName("restaurant_emergency_status")
    var restaurant_emergency_status : Int = 0,
    @SerializedName("restaurant_image")
    var restaurant_image : String = "",
    @SerializedName("additional_delivery_fee")
    var additional_delivery_fee : Double = 0.0,
    @SerializedName("orders_count")
    var order_count: String = "",
    @SerializedName("restaurant_address")
    var restaurant_address: String = ""
)
