package com.joy.fattyfood.data.apiService

import com.joy.fattyfood.domain.responses.DeliverRatingResponse
import com.joy.fattyfood.domain.responses.RatingResponse
import com.joy.fattyfood.domain.responses.RatingValueResponse
import com.joy.fattyfood.utils.helper.ApiRouteConstant
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface DeliveryRatingService {

    //@POST("api/v1/fatty/202221/lashio/main/admin/customers/orders/reviews")
    @POST(ApiRouteConstant.routeOrderReview)
    @FormUrlEncoded
    suspend fun feedbackDeliveryService(
        @Field("order_id") order_id: Int,
        @Field("rating") rating: Int,
        @Field("description") description: String
    ): Response<DeliverRatingResponse>

    //@POST("api/v2/fatty/202221/lashio/main/admin/rating-values")
    @POST(ApiRouteConstant.routeRatingValue)
    suspend fun getRatingValues(): Response<RatingValueResponse>

    //@POST("api/v2/fatty/202221/lashio/main/admin/rating")
    @POST(ApiRouteConstant.routeAdminRating)
    @FormUrlEncoded
    suspend fun rating(
        @Field("rating_type") rating_type: String,
        @Field("order_id") order_id: Int,
        @Field("star") rating: String,
        @Field("options") options: String,
        @Field("comment") description: String,
        @Field("option_locale") locale: String
    ): Response<RatingResponse>
}