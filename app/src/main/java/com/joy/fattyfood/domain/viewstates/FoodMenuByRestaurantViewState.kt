package com.joy.fattyfood.domain.viewstates

import com.joy.fattyfood.domain.responses.*

sealed class FoodMenuByRestaurantViewState{
    object OnLoadingFoodMenuByRestaurant :  FoodMenuByRestaurantViewState()
    data class OnSuccessFoodMenuByRestaurant(val data : FoodMenuByRestaurantResponse) : FoodMenuByRestaurantViewState()
    data class OnFailFoodMenuByRestaurant(val message : String) : FoodMenuByRestaurantViewState()

    object OnLoadingOperateWishList :  FoodMenuByRestaurantViewState()
    data class OnSuccessOperateWishList(val data : OperateWishListResponse) : FoodMenuByRestaurantViewState()
    data class OnFailOperateWishList(val message : String) : FoodMenuByRestaurantViewState()
}
