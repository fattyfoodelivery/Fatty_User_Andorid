package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class ParcelAvailableRegionVO(
    @SerializedName("parcel_block_id")
    var parcel_block_id: Int = 0,
    @SerializedName("block_name")
    var block_name: String = "",
    @SerializedName("latitude")
    var latitude: Double? = 0.0,
    @SerializedName("longitude")
    var longitude: Double? = 0.0,
    var isRegionSelected: Boolean = false
)
