package com.joy.fattyfood.domain.viewstates

import com.joy.fattyfood.domain.responses.*

sealed class RestaurantFoodTypeByMenuIdViewState{
    object OnLoadingRestaurantFoodTypeByMenuId :  RestaurantFoodTypeByMenuIdViewState()
    data class OnSuccessRestaurantFoodTypeByMenuId(val data : RestaurantFoodResponse) : RestaurantFoodTypeByMenuIdViewState()
    data class OnFailRestaurantFoodTypeByMenuId(val message : String) : RestaurantFoodTypeByMenuIdViewState()
}
