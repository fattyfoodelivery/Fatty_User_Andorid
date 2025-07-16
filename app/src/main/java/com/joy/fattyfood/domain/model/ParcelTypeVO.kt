package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class ParcelTypeVO(
    @SerializedName("parcel_type_id")
    var parcel_type_id: Int = 0,
    @SerializedName("parcel_type_name")
    var parcel_type_name: String = "",
    @SerializedName("parcel_type_image")
    var parcel_type_image: String = "",
    var isSelected: Boolean = false
)

data class ParcelExtraVO(
    @SerializedName("parcel_extra_cover_id")
    var parcel_extra_cover_id : Int =0,
    @SerializedName("parcel_extra_cover_image")
    var parcel_extra_cover_image : String ="",
    @SerializedName("parcel_extra_cover_price")
    var parcel_extra_cover_price : Double =0.0
)

data class ParcelImageVO(
    @SerializedName("parcle_image_id")
    var parcle_image_id :Int =0,
    @SerializedName("parcel_image")
    var parcel_image : String =""
)

data class PaymentMethodVO(
    @SerializedName("payment_method_id")
    var payment_method_id : Int =0,
    @SerializedName("payment_method_name")
    var payment_method_name : String =""
)

