package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.responses.DeliverRatingResponse
import com.joy.fattyfood.domain.responses.RatingResponse
import com.joy.fattyfood.domain.responses.RatingValueResponse
import retrofit2.Response

interface DeliveryRatingRepository {

    suspend fun deliveryRating(
        orderId: Int,
        rating: Int,
        description: String
    ): Response<DeliverRatingResponse>

    suspend fun fetchRating(): Response<RatingValueResponse>

    suspend fun rating(
        rating_type: String,
        order_id: Int,
        rating: String,
        options: String,
        description: String,
        locale: String
    ): Response<RatingResponse>
}