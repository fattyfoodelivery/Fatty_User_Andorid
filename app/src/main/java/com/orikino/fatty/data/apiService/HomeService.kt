package com.orikino.fatty.data.apiService

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.utils.helper.ApiRouteConstant
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface HomeService {
    @POST(ApiRouteConstant.routeHome)
    @FormUrlEncoded
    suspend fun fetchHome(
        @Field("customer_id")customer_id: Int,
        @Field("state_name") state_name : String,
        @Field("latitude")latitude: Double,
        @Field("longitude")longitude: Double
    ) : Response<HomeResponse>

    @POST(ApiRouteConstant.routeServiceItem)
    suspend fun fetchServiceItem(
    ) : Response<ServiceItemResponse>

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
        @Field("longitude")longitude: Double,
        @Field("page") page : Int = 1,
        @Field("pageSize") pageSize : Int = 10
    ) : Response<TopRelatedCategoryResponse>

    @GET(ApiRouteConstant.routeCurrencyList)
    suspend fun fetchCurrency() : Response<CurrencyResponse>

    @POST(ApiRouteConstant.routeWishList)
    @FormUrlEncoded
    suspend fun fetchWishList(
        @Field("customer_id") customer_id: Int,
        @Field("latitude") latitude : Double,
        @Field("longitude") longitude : Double
    ): Response<WishListResponse>


    @POST(ApiRouteConstant.routeCustomerWishList)
    @FormUrlEncoded
    suspend fun operateWishList(
        @Field("customer_id") customer_id: Int,
        @Field("restaurant_id") restaurant_id: Int
    ): Response<OperateWishListResponse>





}