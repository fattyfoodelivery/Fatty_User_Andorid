package com.orikino.fatty.domain.responses

import com.orikino.fatty.domain.model.ActiveOrderVO

data class OrderDetailWithRatingResponse(
    val `data`: ActiveOrderVO? = ActiveOrderVO(),
    val message: String?,
    val status: Int?,
    val success: Boolean?
)