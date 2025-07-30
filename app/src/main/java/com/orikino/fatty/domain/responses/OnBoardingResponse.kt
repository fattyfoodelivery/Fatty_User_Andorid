package com.orikino.fatty.domain.responses


data class OnBoardingResponse(
    var `data`: Data?,
    var message: String = "",
    var success: Boolean = false
) {
    data class Data(
        val displayTypeId: Int,
        val image: String,
        val merchantAdsId: Int?,
        val restaurantId: Int?,
        val displayTypeImage: String?,
        val displayTypeDescription: String?,
        val adsTypeId: Int?,
        val restaurantName: String?
    )
}