package com.joy.fattyfood.data.apiService

import com.joy.fattyfood.domain.responses.*
import com.joy.fattyfood.utils.helper.ApiRouteConstant
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface WishListService {

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