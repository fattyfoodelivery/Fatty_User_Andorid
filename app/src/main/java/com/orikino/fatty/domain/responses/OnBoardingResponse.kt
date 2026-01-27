package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName


data class OnBoardingResponse(
    var `data`: Data?,
    var message: String = "",
    var success: Boolean = false
) {
    data class Data(
        @SerializedName("display_type_id")
        val display_type_id: Int,
        @SerializedName("image")
        var image: String,
        @SerializedName("merchant_ads_id")
        val merchant_ads_id: Int?,
        @SerializedName("restaurant_id")
        val restaurant_id: Int?,
        @SerializedName("display_type_name")
        val display_type_name: String?,
        @SerializedName("display_type_description")
        val display_type_description: String?,
        @SerializedName("ads_type_id")
        val ads_type_id: Int?,
        @SerializedName("restaurant_name")
        val restaurant_name: String?
    )
}