package com.joy.fattyfood.domain.viewstates

import com.joy.fattyfood.domain.responses.*

sealed class RecommendedRestaurantListViewState{
    object OnLoadingRecommendedRestaurantList :  com.joy.fattyfood.domain.viewstates.RecommendedRestaurantListViewState()
    data class OnSuccessRecommendedRestaurantList(val data : RecommendedRestaurantListResponse) : com.joy.fattyfood.domain.viewstates.RecommendedRestaurantListViewState()
    data class OnFailRecommendedRestaurantList(val message : String) : com.joy.fattyfood.domain.viewstates.RecommendedRestaurantListViewState()

    object OnLoadingOperateWishList :  com.joy.fattyfood.domain.viewstates.RecommendedRestaurantListViewState()
    data class OnSuccessOperateWishList(val data : OperateWishListResponse) : com.joy.fattyfood.domain.viewstates.RecommendedRestaurantListViewState()
    data class OnFailOperateWishList(val message : String) : com.joy.fattyfood.domain.viewstates.RecommendedRestaurantListViewState()


    object OnLoadingAdsEngagement :  com.joy.fattyfood.domain.viewstates.RecommendedRestaurantListViewState()
    data class OnSuccessAdsEngagement(val data : AdsEngagementResponse) : com.joy.fattyfood.domain.viewstates.RecommendedRestaurantListViewState()
    data class OnFailAdsEngagement(val message : String) : com.joy.fattyfood.domain.viewstates.RecommendedRestaurantListViewState()



}
