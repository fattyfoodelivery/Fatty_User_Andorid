package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*

sealed class CategoryByCategoryIdViewState {
    object OnLoadingCategoryByCategoryId : com.orikino.fatty.domain.viewstates.CategoryByCategoryIdViewState()
    data class OnSuccessCategoryByCategoryId(val data: CategoryByCategoryIdResponse) : com.orikino.fatty.domain.viewstates.CategoryByCategoryIdViewState()
    data class OnFailCategoryByCategoryId(val message: String) : com.orikino.fatty.domain.viewstates.CategoryByCategoryIdViewState()

    object OnLoadingOperateWishList :  com.orikino.fatty.domain.viewstates.CategoryByCategoryIdViewState()
    data class OnSuccessOperateWishList(val data : OperateWishListResponse) : com.orikino.fatty.domain.viewstates.CategoryByCategoryIdViewState()
    data class OnFailOperateWishList(val message : String) : com.orikino.fatty.domain.viewstates.CategoryByCategoryIdViewState()

    object OnLoadingAdsEngagement : com.orikino.fatty.domain.viewstates.CategoryByCategoryIdViewState()
    data class OnSuccessAdsEngagement(val data : AdsEngagementResponse) : com.orikino.fatty.domain.viewstates.CategoryByCategoryIdViewState()
    data class OnFailAdsEngagement(val message : String) : com.orikino.fatty.domain.viewstates.CategoryByCategoryIdViewState()

}