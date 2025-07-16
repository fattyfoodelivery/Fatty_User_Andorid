package com.joy.fattyfood.domain.responses

import com.joy.fattyfood.domain.model.ActiveOrderVO

data class OrderDetailWithRatingResponse(
    val `data`: ActiveOrderVO? = ActiveOrderVO(),
    val message: String?,
    val status: Int?,
    val success: Boolean?
)