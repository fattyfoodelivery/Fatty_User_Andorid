package com.orikino.fatty.domain.responses

data class ParcelOrderResponse(
    val `data`: Data?,
    val message: String?,
    val success: Boolean?
) {
    data class Data(
        val order_id: Int?
    )
}