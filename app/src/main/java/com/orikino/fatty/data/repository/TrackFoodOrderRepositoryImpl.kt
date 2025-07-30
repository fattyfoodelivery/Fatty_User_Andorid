package com.orikino.fatty.data.repository

import com.orikino.fatty.data.apiService.TrackOrderApi
import com.orikino.fatty.data.exceptions.AppDefaultException
import com.orikino.fatty.data.exceptions.AppHttpException
import com.orikino.fatty.domain.responses.RiderLocationResponse
import com.orikino.fatty.domain.responses.TrackFoodOrderResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class TrackFoodOrderRepositoryImpl @Inject constructor(
    private val service : TrackOrderApi
) : TrackFoodOrderRepository {
    override suspend fun trackFoodOrder(order_id: Int): Flow<TrackFoodOrderResponse> {
        var response = TrackFoodOrderResponse()
        return flow {
            withContext(Dispatchers.IO) {
                try {
                    response = service.trackFoodOrder(order_id)
                } catch (e: Throwable) {
                    when (e) {
                        is AppHttpException -> throw AppHttpException("Connection Issue")
                        is HttpException -> throw when {
                            e.response()
                                ?.code() == 500 -> AppDefaultException("Server Error")
                            e.response()
                                ?.code() == 403 -> AppDefaultException("DENIED")
                            e.response()?.code() == 406 -> AppDefaultException("Another Login")
                            else -> AppDefaultException("Something Wrong")
                        }
                        else -> throw AppDefaultException("Failed")
                    }
                }
            }
            emit(response)
        }
    }

    override suspend fun fetchRiderLocation(rider_id: Int): Flow<RiderLocationResponse> {
        var response = RiderLocationResponse()
        return flow {
            withContext(Dispatchers.IO) {
                try {
                    response = service.fetchRiderLocation(rider_id)
                } catch (e: Throwable) {
                    when (e) {
                        is AppHttpException -> throw AppHttpException("Connection Issue")
                        is HttpException -> throw when {
                            e.response()
                                ?.code() == 500 -> AppDefaultException("Server Error")
                            e.response()
                                ?.code() == 403 -> AppDefaultException("DENIED")
                            else -> AppDefaultException("Another Login")
                        }
                        else -> throw AppDefaultException("Failed")
                    }
                }
            }
            emit(response)
        }
    }


}