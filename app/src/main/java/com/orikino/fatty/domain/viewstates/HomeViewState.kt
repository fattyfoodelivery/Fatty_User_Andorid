package com.orikino.fatty

import com.orikino.fatty.domain.responses.*

sealed class HomeViewState{
    object OnLoadingUpdateUserInfo :  HomeViewState()
    data class OnSuccessUpdateUserInfo(val data : UpdateUserInfoResponse) : HomeViewState()
    data class OnFailUpdateUserInfo(val message: String) :HomeViewState()

    data object OnLoadingHome : HomeViewState()
    data class OnSuccessHome(val data : HomeResponse) : HomeViewState()
    data class OnFailHome(val message: String?) : HomeViewState()

    object OnLoadingTopRelated : HomeViewState()
    data class OnSuccessTopRelated(val data : TopRelatedCategoryResponse) : HomeViewState()
    data class OnFailTopRelated(val message: String) : HomeViewState()

    object OnListEndReachTopRelated : HomeViewState()

    object OnLoadingRestaurantByCategory : HomeViewState()
    data class OnSuccessRestaurantByCategory(val data : CategoryByCategoryIdResponse) : HomeViewState()
    data class OnFailRestaurantByCategory(val message: String) : HomeViewState()

    object OnLoadingOperateWishList :  HomeViewState()
    data class OnSuccessOperateWishList(val data : OperateWishListResponse) : HomeViewState()
    data class OnFailOperateWishList(val message : String) : HomeViewState()

    object OnLoadingCurrency :  HomeViewState()
    data class OnSuccessCurrency(val data : CurrencyResponse) : HomeViewState()
    data class OnFailCurrency(val message: String) : HomeViewState()

    object OnLoadingWishList : HomeViewState()
    data class OnSuccessWishList(val data : WishListResponse) : HomeViewState()
    data class OnFailWishList(val message : String) : HomeViewState()


    object OnAdsEngagement : HomeViewState()
    data class OnSuccessAdsEngagement(val data : AdsEngagementResponse) : HomeViewState()
    data class OnFailAdsEngagement(val message : String) : HomeViewState()

}
