package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class AddExtraCoverCostVO(
    @SerializedName("parcel_extra_cover_id")
    var parcel_extra_cover_id: Int = 0,
    @SerializedName("parcel_extra_cover_price")
    var parcel_extra_cover_price: Int = 0,
    @SerializedName("parcel_extra_cover_image")
    var parcel_extra_cover_image: String = "",
    @SerializedName("currency_id")
    var currency_id : Int = 0,
    var isSelected: Boolean = false
)
