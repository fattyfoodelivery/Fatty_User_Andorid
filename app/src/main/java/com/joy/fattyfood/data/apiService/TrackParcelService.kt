package com.joy.fattyfood.data.apiService

import com.joy.fattyfood.domain.responses.RiderLocationResponse
import com.joy.fattyfood.domain.responses.TrackFoodOrderResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TrackOrderApi {
    @POST("api/v2/fatty/202221/lashio/main/admin/customer/orders/click")
    @FormUrlEncoded
    suspend fun trackFoodOrder(@Field("order_id") order_id: Int) : TrackFoodOrderResponse

    @POST("api/v1/fatty/202221/lashio/main/admin/rider/location")
    @FormUrlEncoded
    suspend fun fetchRiderLocation(@Field("rider_id") rider_id: Int) : RiderLocationResponse
}

interface TrackParcelApi{
    @POST("api/v2/fatty/202221/lashio/main/admin/customer/orders/click")
    @FormUrlEncoded
    suspend fun trackParcelOrder(@Field("order_id") order_id: Int) : TrackFoodOrderResponse

    @POST("api/v1/fatty/202221/lashio/main/admin/rider/location")
    @FormUrlEncoded
    suspend fun fetchRiderLocation(@Field("rider_id") rider_id: Int) : RiderLocationResponse
}