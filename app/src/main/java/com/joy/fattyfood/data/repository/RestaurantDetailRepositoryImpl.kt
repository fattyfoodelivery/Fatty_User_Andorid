package com.joy.fattyfood.data.repository

import com.joy.fattyfood.data.apiService.RestaurantDetailService
import com.joy.fattyfood.domain.responses.FoodMenuByRestaurantResponse
import com.joy.fattyfood.domain.responses.OperateWishListResponse
import com.joy.fattyfood.domain.responses.RestDetailReviewListResponse
import com.joy.fattyfood.domain.responses.WishListResponse
import retrofit2.Response
import javax.inject.Inject

class RestaurantDetailRepositoryImpl @Inject constructor(
    private val restDetailApi : RestaurantDetailService
) : RestaurantDetailRepository {
    override suspend fun fetchFoodByRestaurantByMenuById(
        restaurant_id: Int,
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ): Response<FoodMenuByRestaurantResponse> = restDetailApi.fetchFoodMenuByRestaurant(
        restaurant_id,
        customer_id,
        latitude,
        longitude
    )

    override suspend fun fetchRestDetailReviewList(
        restaurant_id: Int,
        page: Int
    ): Response<RestDetailReviewListResponse> = restDetailApi.restaurantDetailReviewList(
        restaurant_id,page
    )

    override suspend fun fetchOperateWishList(
        customer_id: Int,
        restaurant_id: Int
    ): Response<OperateWishListResponse> = restDetailApi.operateWishList(customer_id, restaurant_id)

    override suspend fun fetchWishList(
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ): Response<WishListResponse> = restDetailApi.fetchWishList(customer_id,latitude,longitude)
}