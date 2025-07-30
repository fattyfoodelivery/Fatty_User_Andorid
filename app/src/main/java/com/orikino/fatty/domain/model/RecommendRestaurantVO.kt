package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class RecommendRestaurantVO(
    @SerializedName("listing_type")
    var listing_type : Int = 0,
    @SerializedName("merchant_ads_id")
    var merchant_ads_id: Int = 0,
    @SerializedName("display_type_id")
    var display_type_id: Int = 0,
    @SerializedName("display_type_image")
    var display_type_image: String = "",
    @SerializedName("display_type_description")
    var display_type_description : String = "",
    @SerializedName("image")
    var image : String = "",
    @SerializedName("ads_type_id")
    var ads_type_id : Int = 0,
    @SerializedName("restaurant_name")
    var restaurant_name : String = "",
    @SerializedName("orders_count")
    var orders_count : Int = 0,
    @SerializedName("restaurant_category_name")
    var restaurant_category_name: String = "",

    @SerializedName("Address")
    var address : String = "",
    @SerializedName("rating")
    var rating : Double = 0.0,
    @SerializedName("restaurant_id")
    var restaurant_id : Int = 0,
    @SerializedName("restaurant_name_mm")
    var restaurant_name_mm : String ?= null ?: "hhh",
    @SerializedName("restaurant_name_en")
    var restaurant_name_en : String ?= null ?:"ddd",
    @SerializedName("restaurant_name_ch")
    var restaurant_name_ch : String ?= null ?:"",
    @SerializedName("restaurant_address")
    var restaurant_address : String ?= null,
    @SerializedName("restaurant_address_mm")
    var restaurant_address_mm : String ?= null,
    @SerializedName("restaurant_category_name_mm")
    var restaurant_category_name_mm :String ?= null,
    @SerializedName("restaurant_category_name_en")
    var restaurant_category_name_en : String ?= null,
    @SerializedName("restaurant_category_name_ch")
    var restaurant_category_name_ch : String ?= null,
    @SerializedName("restaurant_address_en")
    var restaurant_address_en : String ?= null,
    @SerializedName("restaurant_address_ch")
    var restaurant_address_ch : String ?= null,
    @SerializedName("restaurant_latitude")
    var restaurant_latitude : Double =0.0,
    @SerializedName("restaurant_longitude")
    var restaurant_longitude : Double =0.0,
    @SerializedName("category")
    var category : CategoryVO = CategoryVO(),
    @SerializedName("food")
    var food : MutableList<FoodVO> = mutableListOf(),
    @SerializedName("restaurant_image")
    var restaurant_image : String = "",
    @SerializedName("restaurant_emergency_status")
    var restaurant_emergency_status : Int = 0,
    @SerializedName("wishlist")
    var wishlist : Int = 0,
    @SerializedName("is_wish")
    var is_wish : Boolean = false,
    @SerializedName("distance")
    var distance : Double = 0.0,
    @SerializedName("define_amount")
    var define_amount : Double = 0.0,
    @SerializedName("limit_distance")
    var limit_distance : Double = 0.0,
    @SerializedName("distance_time")
    var distance_time : Int = 0,
    @SerializedName("delivery_fee")
    var delivery_fee : Double = 0.0,
    @SerializedName("average_time")
    var average_time : Int = 0,
    @SerializedName("currency_type")
    var currency_type : String = "",
    @SerializedName("city_id")
    var city_id : Int = 0,
    @SerializedName("additional_delivery_fee")
    var additional_delivery_fee : Double = 0.0
)
