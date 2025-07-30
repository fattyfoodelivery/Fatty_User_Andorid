package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class TopRelatedCategoryVO(
    @SerializedName("restaurant_id")
    var restaurant_id : Int = 0,
    @SerializedName("restaurant_name")
    var restaurant_name : String = "",
    @SerializedName("rating")
    var rating : Double = 0.0 ,
    @SerializedName("restaurant_category_id")
    var restaurant_category_id : Int = 0 ,
    @SerializedName("is_wish")
    var is_wish : Boolean = false,
    @SerializedName("distance")
    var distance : Double = 0.0,
    @SerializedName("distance_time")
    var distance_time : Double = 0.0,
    @SerializedName("restaurant_emergency_status")
    var restaurant_emergency_status : Int = 0,
    @SerializedName("restaurant_image")
    var restaurant_image : String = "",
    @SerializedName("orders_count")
    var orders_count : String = "",
    @SerializedName("Address")
    var address : String = ""
)
