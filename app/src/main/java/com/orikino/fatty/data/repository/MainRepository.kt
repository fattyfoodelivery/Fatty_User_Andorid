package com.orikino.fatty.data.repository

import com.orikino.fatty.domain.responses.*
import retrofit2.Response

interface MainRepository {

    suspend fun updateUserInfo(
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ) : Response<UpdateUserInfoResponse>

    suspend fun fetchTopRelatedCategory(
        customer_id: Int,
        latitude: Double,
        longitude: Double,
    ) : Response<TopRelatedCategoryResponse>

    suspend fun fetchCurrentCurrency() : Response<CurrencyResponse>
}