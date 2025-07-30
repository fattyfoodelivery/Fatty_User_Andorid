package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*

sealed class RestaurantViewState {

    /*object OnLoadingRestaurantDetail :  RestaurantViewState()
    data class OnSuccessRestaurantDetail(val data : RestaurantDetailResponse) : RestaurantViewState()
    data class OnFailRestaurantDetail(val message: String) : RestaurantViewState()*/
    object OnLoadingFoodMenuByRestaurant : RestaurantViewState()
    data class OnSuccessFoodMenuByRestaurant(val data : FoodMenuByRestaurantResponse) :RestaurantViewState()
    data class OnFailFoodMenuByRestaurant(val message: String?) : RestaurantViewState()


    /*object OnLoadingRestaurantDetail :  RestaurantViewState()
    data class OnSuccessRestaurantDetail(val data : RestaurantDetailResponse) : RestaurantViewState()
    data class OnFailRestaurantDetail(val code : Int) : RestaurantViewState()*/



    /*object OnLoadingOperateWishList :  RestaurantViewState()
    data class OnSuccessOperateWishList(val data : OperateWishListResponse) : RestaurantViewState()
    data class OnFailOperateWishList(val code : Int) : RestaurantViewState()*/


}
