package com.orikino.fatty.data.apiService

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.utils.helper.ApiRouteConstant
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ParcelService {

    @POST(ApiRouteConstant.routeParcelOrder)
    @FormUrlEncoded
    suspend fun parcelOrder(
        @Field("customer_id") userId : Int,
        @Field("parcel_zone_id") zoneID : Int
    ) : Response<ParcelOrderInfoResponse>

    @GET(ApiRouteConstant.routeCurrencyList)
    suspend fun fetchCurrency() : Response<CurrencyResponse>

}