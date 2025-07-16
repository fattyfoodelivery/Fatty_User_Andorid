package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.responses.FoodMenuByRestaurantResponse
import com.joy.fattyfood.domain.responses.OperateWishListResponse
import com.joy.fattyfood.domain.responses.RestDetailReviewListResponse
import com.joy.fattyfood.domain.responses.WishListResponse
import retrofit2.Response


interface RestaurantDetailRepository {

    suspend fun fetchFoodByRestaurantByMenuById(
        restaurant_id : Int,
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ) : Response<FoodMenuByRestaurantResponse>

    suspend fun fetchRestDetailReviewList(
        restaurant_id: Int,
        page : Int,
    ) : Response<RestDetailReviewListResponse>

    suspend fun fetchOperateWishList(
        customer_id: Int,
        restaurant_id: Int
    ) : Response<OperateWishListResponse>

    suspend fun fetchWishList(
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ) : Response<WishListResponse>
}