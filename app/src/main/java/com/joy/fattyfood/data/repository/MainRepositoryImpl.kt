package com.joy.fattyfood.data.repository

import com.joy.fattyfood.data.apiService.MainService
import com.joy.fattyfood.domain.responses.*
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