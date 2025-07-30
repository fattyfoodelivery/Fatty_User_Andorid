package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class CustomerAddressVO(
    @SerializedName("customer_address_id")
    var customer_address_id: Int = 0,
    @SerializedName("customer_id")
    var customer_id: Int = 0,
    @SerializedName("address_latitude")
    var address_latitude: Double = 0.0,
    @SerializedName("address_longitude")
    var address_longitude: Double = 0.0,
    @SerializedName("current_address")
    var current_address: String = "",
    @SerializedName("customer_phone")
    var customer_phone: String = "",
    @SerializedName("building_system")
    var building_system: String? = null,
    @SerializedName("address_type")
    var address_type: String = "",
    @SerializedName("is_default")
    var is_default: Boolean = false,
    var onTapPositon : Boolean = false
)
