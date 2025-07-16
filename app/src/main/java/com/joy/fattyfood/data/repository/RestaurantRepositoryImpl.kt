package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.responses.*
import com.joy.fattyfood.data.apiService.RestaurantService
import retrofit2.Response
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val server : RestaurantService
) : RestaurantRepository {
    override suspend fun fetchFoodMenuByRestaurant(
        restaurant_id: Int,
        customer_id: Int,
        latitude : Double,
        longitude : Double
    ): Response<FoodMenuByRestaurantResponse> = server.fetchFoodMenuByRestaurant(restaurant_id,customer_id,latitude,longitude)

    override suspend fun fetchRestaurantDetail(restaurant_id: Int): Response<RestaurantDetailResponse> = server.fetchRestaurantDetail(restaurant_id)


    /*override suspend fun restaurantFoodTypeByMenuId(food_menu_id: Int):
            Response<RestaurantFoodResponse> = server.fetchRestaurantFoodTypeByMenuId(food_menu_id)*/
}