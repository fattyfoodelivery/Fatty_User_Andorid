package com.joy.fattyfood.domain.viewstates

import com.joy.fattyfood.domain.responses.*

sealed class CategoryByCategoryIdViewState {
    object OnLoadingCategoryByCategoryId : com.joy.fattyfood.domain.viewstates.CategoryByCategoryIdViewState()
    data class OnSuccessCategoryByCategoryId(val data: CategoryByCategoryIdResponse) : com.joy.fattyfood.domain.viewstates.CategoryByCategoryIdViewState()
    data class OnFailCategoryByCategoryId(val message: String) : com.joy.fattyfood.domain.viewstates.CategoryByCategoryIdViewState()

    object OnLoadingOperateWishList :  com.joy.fattyfood.domain.viewstates.CategoryByCategoryIdViewState()
    data class OnSuccessOperateWishList(val data : OperateWishListResponse) : com.joy.fattyfood.domain.viewstates.CategoryByCategoryIdViewState()
    data class OnFailOperateWishList(val message : String) : com.joy.fattyfood.domain.viewstates.CategoryByCategoryIdViewState()

    object OnLoadingAdsEngagement : com.joy.fattyfood.domain.viewstates.CategoryByCategoryIdViewState()
    data class OnSuccessAdsEngagement(val data : AdsEngagementResponse) : com.joy.fattyfood.domain.viewstates.CategoryByCategoryIdViewState()
    data class OnFailAdsEngagement(val message : String) : com.joy.fattyfood.domain.viewstates.CategoryByCategoryIdViewState()

}