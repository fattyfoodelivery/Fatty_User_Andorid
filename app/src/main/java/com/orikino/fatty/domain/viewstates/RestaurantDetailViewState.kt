package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.FoodMenuByRestaurantResponse
import com.orikino.fatty.domain.responses.OperateWishListResponse
import com.orikino.fatty.domain.responses.RestDetailReviewListResponse
import com.orikino.fatty.domain.responses.WishListResponse

sealed class RestaurantDetailViewState {

    object OnLoadingRestFoodMenuByRestID : RestaurantDetailViewState()
    data class OnSuccessRestFoodMenuByRestID(val data : FoodMenuByRestaurantResponse) : RestaurantDetailViewState()
    data class OnFailRestFoodMenuByRestID(val message : String?) : RestaurantDetailViewState()

    object OnLoadingRestReviewList : RestaurantDetailViewState()
    data class OnSuccessRestReviewList(val data : RestDetailReviewListResponse) : RestaurantDetailViewState()
    data class OnFailRestRestReviewList(val message : String?) : RestaurantDetailViewState()

    object OnLoadingOperateWishList : RestaurantDetailViewState()
    data class OnSuccessOperateList(val data : OperateWishListResponse) : RestaurantDetailViewState()
    data class OnFailOperateList(val message : String) : RestaurantDetailViewState()

    object OnLoadingFetchWishList : RestaurantDetailViewState()
    data class OnSuccessFetchList(val data : WishListResponse) : RestaurantDetailViewState()
    data class OnFailFetchWishList(val message : String) : RestaurantDetailViewState()
}