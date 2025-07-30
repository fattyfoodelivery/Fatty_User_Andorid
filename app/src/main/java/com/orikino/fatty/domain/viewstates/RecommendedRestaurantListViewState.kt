package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*

sealed class RecommendedRestaurantListViewState{
    object OnLoadingRecommendedRestaurantList :  com.orikino.fatty.domain.viewstates.RecommendedRestaurantListViewState()
    data class OnSuccessRecommendedRestaurantList(val data : RecommendedRestaurantListResponse) : com.orikino.fatty.domain.viewstates.RecommendedRestaurantListViewState()
    data class OnFailRecommendedRestaurantList(val message : String) : com.orikino.fatty.domain.viewstates.RecommendedRestaurantListViewState()

    object OnLoadingOperateWishList :  com.orikino.fatty.domain.viewstates.RecommendedRestaurantListViewState()
    data class OnSuccessOperateWishList(val data : OperateWishListResponse) : com.orikino.fatty.domain.viewstates.RecommendedRestaurantListViewState()
    data class OnFailOperateWishList(val message : String) : com.orikino.fatty.domain.viewstates.RecommendedRestaurantListViewState()


    object OnLoadingAdsEngagement :  com.orikino.fatty.domain.viewstates.RecommendedRestaurantListViewState()
    data class OnSuccessAdsEngagement(val data : AdsEngagementResponse) : com.orikino.fatty.domain.viewstates.RecommendedRestaurantListViewState()
    data class OnFailAdsEngagement(val message : String) : com.orikino.fatty.domain.viewstates.RecommendedRestaurantListViewState()



}
