package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*

sealed class RestaurantFoodTypeByMenuIdViewState{
    object OnLoadingRestaurantFoodTypeByMenuId :  RestaurantFoodTypeByMenuIdViewState()
    data class OnSuccessRestaurantFoodTypeByMenuId(val data : RestaurantFoodResponse) : RestaurantFoodTypeByMenuIdViewState()
    data class OnFailRestaurantFoodTypeByMenuId(val message : String) : RestaurantFoodTypeByMenuIdViewState()
}
