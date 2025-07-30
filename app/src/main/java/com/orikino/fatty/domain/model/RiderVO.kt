package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class RiderVO(
    @SerializedName("rider_id")
    var rider_id : Int = 0,
    @SerializedName("rider_user_name")
    var rider_user_name : String ="",
    @SerializedName("rider_user_phone")
    var rider_user_phone : String ="",
    @SerializedName("rider_image")
    var rider_image : String ?= null,
    @SerializedName("rider_latitude")
    var rider_latitude : Double = 0.0,
    @SerializedName("rider_longitude")
    var rider_longitude : Double = 0.0
)
