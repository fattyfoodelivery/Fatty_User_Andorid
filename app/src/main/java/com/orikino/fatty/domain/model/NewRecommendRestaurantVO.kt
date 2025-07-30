package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class NewRecommendRestaurantVO(
    @SerializedName("merchant_ads_id")
    var merchant_ads_id : Int = 0,
    @SerializedName("restaurant_id")
    var restaurant_id : Int = 0,
    @SerializedName("restaurant_name")
    var restaurant_name : String = "",
    @SerializedName("image")
    var image : String = "" ,
    @SerializedName("display_type_id")
    var display_type_id : Int = 0,
    @SerializedName("display_type_image")
    var display_type_image : String = "",
    @SerializedName("display_type_description")
    var display_type_description : String? = null,
    @SerializedName("restaurant_emergency_status")
    var restaurant_emergency_status : Int = 0
)
