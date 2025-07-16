package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.responses.*
import retrofit2.Response


interface HomeRepository {

    suspend fun fetchHome(
        customer_id: Int,
        stateName : String,
        latitude: Double,
        longitude: Double
    ): Response<HomeResponse>

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

    suspend fun fetchCurrency() : Response<CurrencyResponse>

    suspend fun fetchWishList(
        customer_id: Int,
        latitude: Double,
        longitude: Double,
    ) : Response<WishListResponse>
    suspend fun operateWishList(
        customer_id: Int,
        restaurant_id : Int
    ) : Response<OperateWishListResponse>
}