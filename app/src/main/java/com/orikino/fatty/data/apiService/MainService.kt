package com.orikino.fatty.data.apiService

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.utils.helper.ApiRouteConstant
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface MainService {

    @POST(ApiRouteConstant.routeUpdateUserLocation)
    @FormUrlEncoded
    suspend fun updateUserLocation(
        @Field("customer_id")customer_id: Int,
        @Field("latitude")latitude: Double,
        @Field("longitude")longitude: Double
    ) : Response<UpdateUserInfoResponse>


    @POST(ApiRouteConstant.routeTopRelatedCategory)
    @FormUrlEncoded
    suspend fun fetchTopRelatedCategory(
        @Field("customer_id")customer_id: Int,
        @Field("latitude")latitude: Double,
        @Field("longitude")longitude: Double
    ) : Response<TopRelatedCategoryResponse>

    @GET(ApiRouteConstant.routeCurrencyList)
    suspend fun fetchCurrCurrency() : Response<CurrencyResponse>


}