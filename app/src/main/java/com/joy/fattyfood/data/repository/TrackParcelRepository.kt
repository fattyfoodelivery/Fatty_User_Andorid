package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.responses.RiderLocationResponse
import com.joy.fattyfood.domain.responses.TrackFoodOrderResponse
import kotlinx.coroutines.flow.Flow

interface TrackFoodOrderRepository {
    suspend fun trackFoodOrder(order_id : Int) : Flow<TrackFoodOrderResponse>
    suspend fun fetchRiderLocation(rider_id : Int) : Flow<RiderLocationResponse>
}

interface TrackParcelRepository{
    suspend fun trackParcelOrder(order_id : Int) : Flow<TrackFoodOrderResponse>
    suspend fun fetchRiderLocation(rider_id : Int) : Flow<RiderLocationResponse>
}