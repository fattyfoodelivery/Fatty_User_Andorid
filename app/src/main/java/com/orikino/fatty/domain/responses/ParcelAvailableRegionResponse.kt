package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName
import com.orikino.fatty.domain.model.ParcelAvailableRegionDataVO

data class ParcelAvailableRegionResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : ParcelAvailableRegionDataVO = ParcelAvailableRegionDataVO(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)
