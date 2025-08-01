package com.orikino.fatty.data.repository

import com.orikino.fatty.domain.responses.*
import retrofit2.Response

interface RestaurantRepository {

    suspend fun fetchFoodMenuByRestaurant(
        restaurant_id: Int,
        customer_id: Int,
        latitude : Double,
        longitude :Double
    ): Response<FoodMenuByRestaurantResponse>

    suspend fun fetchRestaurantDetail(
        restaurant_id : Int
    ) : Response<RestaurantDetailResponse>

    /*suspend fun restaurantFoodTypeByMenuId(
        food_menu_id : Int
    ): Response<RestaurantFoodResponse>*/


}