package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class CustomerCurrentAddressVO(
    @SerializedName("address_latitude")
    var address_latitude : Double =0.0,
    @SerializedName("address_longitude")
    var address_longitude : Double = 0.0,
    @SerializedName("current_address")
    var current_address : String ?=null,
    @SerializedName("address_type")
    var address_type : String ="",
    @SerializedName("is_default")
    var is_default : Boolean = false
)
