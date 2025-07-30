package com.orikino.fatty.data.repository

import com.orikino.fatty.data.apiService.DeliveryRatingService
import com.orikino.fatty.domain.responses.DeliverRatingResponse
import com.orikino.fatty.domain.responses.RatingResponse
import com.orikino.fatty.domain.responses.RatingValueResponse
import retrofit2.Response
import javax.inject.Inject

class DeliveryRatingRepositoryImpl @Inject constructor(
    private val service: DeliveryRatingService
) : DeliveryRatingRepository {
    override suspend fun deliveryRating(
        orderId: Int,
        rating: Int,
        description: String,
    ): Response<DeliverRatingResponse> = service.feedbackDeliveryService(orderId, rating, description)


    override suspend fun fetchRating(): Response<RatingValueResponse> = service.getRatingValues()

    override suspend fun rating(
        rating_type: String,
        order_id: Int,
        rating: String,
        options: String,
        description: String,
        locale: String,
    ): Response<RatingResponse> = service.rating(rating_type, order_id, rating, options, description, locale)


}