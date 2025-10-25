package com.orikino.fatty.domain.responses

data class ParcelOrderInfoResponse(
    val `data`: Data?,
    val message: String?,
    val success: Boolean?,
    val is_available : Boolean?,
    val start_time : String,
    val end_time : String
) {
    data class Data(
        val order_id: Int?
    )
}