package com.orikino.fatty.data.apiService

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.utils.helper.ApiRouteConstant
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path


interface TrackOrderService {
    @POST(ApiRouteConstant.routeCustomerOrderClick)
    @FormUrlEncoded
    suspend fun trackFoodOrder(@Field("order_id") order_id: Int) : Response<TrackFoodOrderResponse>

    @POST(ApiRouteConstant.routeCustomerOrderClick)
    @FormUrlEncoded
    suspend fun trackParcelOrder(@Field("order_id") order_id: Int) : Response<TrackFoodOrderResponse>

    @POST(ApiRouteConstant.routeRiderLocation)
    @FormUrlEncoded
    suspend fun fetchRiderLocation(@Field("rider_id") rider_id: Int) : Response<RiderLocationResponse>

    @POST("api/v2/fatty/202221/lashio/main/admin/orders/{order_id}")
    suspend fun orderDetailWithRating(@Path("order_id") order_id: String): Response<OrderDetailWithRatingResponse>

    @POST(ApiRouteConstant.routeParcelOrder)
    @FormUrlEncoded
    suspend fun parcelOrder(
        @Field("customer_id") userId : Int,
        @Field("parcel_zone_id") zoneID : Int
    ) : Response<ParcelOrderInfoResponse>
}

