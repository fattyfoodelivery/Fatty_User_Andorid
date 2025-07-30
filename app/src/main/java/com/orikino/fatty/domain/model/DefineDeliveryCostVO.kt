package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class DefineDeliveryCostVO(
    @SerializedName("weight")
    var weight: String = "",
    @SerializedName("delivery_fee")
    var delivery_fee: Double = 0.0
)
