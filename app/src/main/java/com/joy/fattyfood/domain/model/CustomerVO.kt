package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


data class CustomerVO(
    @SerializedName("customer_id")
    var customer_id: Int? = 0,
    @SerializedName("customer_type_id")
    var customer_type_id: Int? = 0,
    @SerializedName("is_restricted")
    var is_restricted: Int? = 0,
    @SerializedName("customer_name")
    var customer_name: String? = "",
    @SerializedName("customer_phone")
    var customer_phone: String = "",
    @SerializedName("image")
    var image: String? = "",
    @SerializedName("latitude")
    var latitude: Double? =16.8667448,
    @SerializedName("longitude")
    var longitude: Double? = 96.1257022,
    @SerializedName("fcm_token")
    var fcm_token: String? = "",
)
