package com.orikino.fatty.data.repository

import com.orikino.fatty.domain.responses.*
import retrofit2.Response

interface TrackOrderRepository {

    suspend fun trackOrderFood(order_id : Int) : Response<TrackFoodOrderResponse>

    suspend fun trackOrderParcel(order_id : Int) : Response<TrackFoodOrderResponse>

    suspend fun fetchRiderLocation(rider_id : Int) : Response<RiderLocationResponse>

    suspend fun trackOrderDetailWithRating(order_id : String) : Response<OrderDetailWithRatingResponse>

    suspend fun parcelOderCheck(customerId : Int,parcel_zone_id : Int) : Response<ParcelOrderInfoResponse>
}