package com.orikino.fatty.data.repository

import com.orikino.fatty.data.apiService.TrackOrderService
import com.orikino.fatty.domain.responses.OrderDetailWithRatingResponse
import com.orikino.fatty.domain.responses.ParcelOrderInfoResponse
import com.orikino.fatty.domain.responses.RiderLocationResponse
import com.orikino.fatty.domain.responses.TrackFoodOrderResponse
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