package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class CityVO(
    @SerializedName("city_id")
    var city_id : Int =0,
    @SerializedName("state_id")
    var state_id : Int = 0,
    @SerializedName("city_name_mm")
    var city_name_mm : String ="",
    @SerializedName("city_name_en")
    var city_name_en : String = ""
)
