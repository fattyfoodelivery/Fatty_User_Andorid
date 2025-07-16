package com.joy.fattyfood

import com.joy.fattyfood.domain.responses.*

sealed class CategoryViewState{
    object OnLoadingCategoryList :  CategoryViewState()
    data class OnSuccessCategoryList(val data : CategoryListResponse) : CategoryViewState()
    data class OnFailCategoryList(val message: String) : CategoryViewState()
}
