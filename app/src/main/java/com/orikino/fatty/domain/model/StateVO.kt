package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class StateVO(
    @SerializedName("state_id")
    var state_id : Int = 0,
    @SerializedName("state_name")
    var state_name : String = "",
    @SerializedName("city_id")
    var city_id : Int = 0,
    @SerializedName("city_name")
    var city_name : String = "",
    @SerializedName("zone_id")
    var zone_id : Int = 0
)
