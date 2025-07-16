package com.joy.fattyfood.data.repository

import com.joy.fattyfood.data.apiService.DeliveryRatingService
import com.joy.fattyfood.domain.responses.DeliverRatingResponse
import com.joy.fattyfood.domain.responses.RatingResponse
import com.joy.fattyfood.domain.responses.RatingValueResponse
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