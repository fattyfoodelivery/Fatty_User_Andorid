package com.orikino.fatty.data.repository

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.data.apiService.ParcelService
import retrofit2.Response
import javax.inject.Inject

class ParcelOrderRepositoryImpl @Inject constructor(
    private val service : ParcelService
) : ParcelRepository {
    override suspend fun parcelOrderById(
        customer_id: Int,
        parcel_zone_id: Int
    ): Response<ParcelOrderInfoResponse> = service.parcelOrder(customer_id,parcel_zone_id)

    override suspend fun fetchCurrency(): Response<CurrencyResponse>  = service.fetchCurrency()

}