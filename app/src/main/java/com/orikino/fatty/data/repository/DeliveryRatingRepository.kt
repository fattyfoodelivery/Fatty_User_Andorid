package com.orikino.fatty.data.repository

import com.orikino.fatty.domain.responses.DeliverRatingResponse
import com.orikino.fatty.domain.responses.RatingResponse
import com.orikino.fatty.domain.responses.RatingValueResponse
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