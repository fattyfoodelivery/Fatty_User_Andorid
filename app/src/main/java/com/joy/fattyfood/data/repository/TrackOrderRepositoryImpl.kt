package com.joy.fattyfood.data.repository

import com.joy.fattyfood.data.apiService.TrackOrderService
import com.joy.fattyfood.domain.responses.OrderDetailWithRatingResponse
import com.joy.fattyfood.domain.responses.ParcelOrderInfoResponse
import com.joy.fattyfood.domain.responses.RiderLocationResponse
import com.joy.fattyfood.domain.responses.TrackFoodOrderResponse
import retrofit2.Response
import javax.inject.Inject

class TrackOrderRepositoryImpl @Inject constructor(
    private val service: TrackOrderService
) : TrackOrderRepository {
    override suspend fun trackOrderFood(order_id: Int): Response<TrackFoodOrderResponse> =
        service.trackFoodOrder(order_id)

    override suspend fun trackOrderParcel(order_id: Int): Response<TrackFoodOrderResponse> =
        service.trackParcelOrder(order_id)

    override suspend fun fetchRiderLocation(rider_id: Int): Response<RiderLocationResponse> =
        service.fetchRiderLocation(rider_id)

    override suspend fun trackOrderDetailWithRating(order_id: String): Response<OrderDetailWithRatingResponse> =
        service.orderDetailWithRating(order_id)
    override suspend fun parcelOderCheck(
        customerId: Int,
        parcel_zone_id: Int
    ): Response<ParcelOrderInfoResponse> = service.parcelOrder(customerId,parcel_zone_id)

}