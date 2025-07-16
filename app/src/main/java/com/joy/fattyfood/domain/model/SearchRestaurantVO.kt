package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class SearchRestaurantVO(
    @SerializedName("restaurant_id")
    var restaurant_id : Int = 0,
    @SerializedName("restaurant_name_mm")
    var restaurant_name_mm : String ?= null,
    @SerializedName("restaurant_name_en")
    var restaurant_name_en : String ?= null,
    @SerializedName("restaurant_name_ch")
    var restaurant_name_ch : String ?= null,
    @SerializedName("restaurant_address_mm")
    var restaurant_address_mm : String ?= null,
    @SerializedName("restaurant_address_en")
    var restaurant_address_en : String ?= null,
    @SerializedName("restaurant_address_ch")
    var restaurant_address_ch : String ?= null,
    @SerializedName("restaurant_category_id")
    var restaurant_category_id : Int = 0,
    @SerializedName("restaurant_category_name_mm")
    var restaurant_category_name_mm :String ?= null,
    @SerializedName("restaurant_category_name_en")
    var restaurant_category_name_en : String ?= null,
    @SerializedName("restaurant_category_name_ch")
    var restaurant_category_name_ch : String ?= null,
    @SerializedName("restaurant_image")
    var restaurant_image : String = "",
    @SerializedName("emergency_status")
    var emergency_status : Int = 0
)
