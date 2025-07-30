package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class FoodMenuByRestaurantVO(
    @SerializedName("restaurant_id")
    var restaurant_id: Int = 0,
    @SerializedName("restaurant_name")
    var restaurant_name: String? = null,
    @SerializedName("restaurant_name_mm")
    var restaurant_name_mm: String? = null,
    @SerializedName("restaurant_name_en")
    var restaurant_name_en: String? = null,
    @SerializedName("restaurant_name_ch")
    var restaurant_name_ch: String? = null,
    @SerializedName("restaurant_address_mm")
    var restaurant_address_mm: String? = null,
    @SerializedName("restaurant_address_en")
    var restaurant_address_en: String? = null,
    @SerializedName("restaurant_address_ch")
    var restaurant_address_ch: String? = null,
    @SerializedName("restaurant_block_id")
    var restaurant_block_id: Int = 0,
    @SerializedName("restaurant_category_id")
    var restaurant_category_id: Int = 0,

    @SerializedName("Address")
    var address : String = "",

    @SerializedName("zone_id")
    var zone_id: Int = 0,
    @SerializedName("city_id")
    var city_id: Int = 0,
    @SerializedName("state_id")
    var state_id: Int = 0,
    @SerializedName("restaurant_latitude")
    var restaurant_latitude : Double =0.0,
    @SerializedName("restaurant_longitude")
    var restaurant_longitude : Double =0.0,
    @SerializedName("restaurant_image")
    var restaurant_image: String ?= null,
    @SerializedName("restaurant_address")
    var restaurant_address: String ?= null,
    @SerializedName("restaurant_phone")
    var restaurant_phone: String ?= null,
    @SerializedName("restaurant_fcm_token")
    var restaurant_fcm_token: String ?= null,
    @SerializedName("is_recommend")
    var is_recommend: Int = 0,
    @SerializedName("restaurant_emergency_status")
    var restaurant_emergency_status: Int = 0,
    @SerializedName("restaurant_user_id")
    var restaurant_user_id: Int = 0,
    @SerializedName("average_time")
    var average_time: Int = 0,
    @SerializedName("rush_hour_time")
    var rush_hour_time: Int = 0,
    @SerializedName("percentage")
    var percentage: Double = 0.0,
    @SerializedName("define_amount")
    var define_amount : Int = 0,
    @SerializedName("additional_delivery_fee")
    var additional_delivery_fee : Int = 0,
    @SerializedName("rating")
    var rating : Double = 0.0,
    @SerializedName("distance")
    var distance : Double = 0.0,
    @SerializedName("wishlist")
    var wishlist : Int = 0,
    @SerializedName("is_wish")
    var is_wish : Boolean = false,
    @SerializedName("limit_distance")
    var limit_distance : Double = 0.0,
    @SerializedName("available_time")
    var available_time : MutableList<AvailableTimeVO> = mutableListOf(),
    @SerializedName("menu")
    val menu: MutableList<MenuVO> = mutableListOf()
//    @SerializedName("currency_type")
//    var currency_type : String =""
//    @SerializedName("available_time")
//    var available_time : MutableList<String> = mutableListOf<String>()

    /*@SerializedName("restaurant_id")
    var restaurant_id: Int = 0,
    @SerializedName("restaurant_name_mm")
    var restaurant_name_mm: String? = null,
    @SerializedName("restaurant_name_en")
    var restaurant_name_en: String? = null,
    @SerializedName("restaurant_name_ch")
    var restaurant_name_ch: String? = null,
    @SerializedName("restaurant_address_mm")
    var restaurant_address_mm: String? = null,
    @SerializedName("restaurant_address_en")
    var restaurant_address_en: String? = null,
    @SerializedName("restaurant_address_ch")
    var restaurant_address_ch: String? = null,
    @SerializedName("restaurant_latitude")
    var restaurant_latitude : Double =0.0,
    @SerializedName("restaurant_longitude")
    var restaurant_longitude : Double =0.0,
    @SerializedName("restaurant_image")
    var restaurant_image: String ?= null,
    @SerializedName("restaurant_emergency_status")
    var restaurant_emergency_status: Int = 0,
    @SerializedName("menu")
    var menu: MutableList<MenuVO> = mutableListOf(),
    @SerializedName("wishlist")
    var wishlist : Int = 0,
    @SerializedName("is_wish")
    var is_wish : Boolean = false,
    @SerializedName("average_time")
    var average_time : Int = 0,
    @SerializedName("available_time")
    var available_time : MutableList<AvailableTime> = mutableListOf(),
    @SerializedName("distance")
    var distance : Double = 0.0,
    @SerializedName("limit_distance")
    var limit_distance : Double = 0.0,
    @SerializedName("rating")
    var rating : Double = 0.0
//    @SerializedName("currency_type")
//    var currency_type : String =""
//    @SerializedName("available_time")
//    var available_time : MutableList<String> = mutableListOf<String>()*/
)

data class AvailableTime(
    @SerializedName("day")
    var day : String? =null,
    @SerializedName("opening_time")
    var opening_time : String? =null,
    @SerializedName("closing_time")
    var closing_time : String? =null
)