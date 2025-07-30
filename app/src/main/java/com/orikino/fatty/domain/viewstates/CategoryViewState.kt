package com.orikino.fatty

import com.orikino.fatty.domain.responses.*

sealed class CategoryViewState{
    object OnLoadingCategoryList :  CategoryViewState()
    data class OnSuccessCategoryList(val data : CategoryListResponse) : CategoryViewState()
    data class OnFailCategoryList(val message: String) : CategoryViewState()
}
