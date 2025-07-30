package com.orikino.fatty.data.repository

import com.orikino.fatty.data.apiService.MainService
import com.orikino.fatty.domain.responses.*
import retrofit2.Response
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val service : MainService
) : MainRepository {
    override suspend fun updateUserInfo(
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ): Response<UpdateUserInfoResponse> = service.updateUserLocation(customer_id,latitude, longitude)

    override suspend fun fetchTopRelatedCategory(
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ): Response<TopRelatedCategoryResponse> = service.fetchTopRelatedCategory(customer_id,latitude,longitude)

    override suspend fun fetchCurrentCurrency(): Response<CurrencyResponse> = service.fetchCurrCurrency()


}