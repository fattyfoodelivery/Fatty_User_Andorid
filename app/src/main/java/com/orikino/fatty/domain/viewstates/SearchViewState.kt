package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*

sealed class SearchViewState{
    object OnLoadingSearchFood : SearchViewState()
    data class OnSuccessSearchFood(val data : SearchFoodResponse) :SearchViewState()
    data class OnFailSearchFood(val message : String) :SearchViewState()

    object OnLoadingSearchFoodAndRestaurant :  SearchViewState()
    data class OnSuccessSearchFoodAndRestaurant(val data : SearchFoodAndRestaurantsResponse) : SearchViewState()
    data class OnFailSearchFoodAndRestaurant(val message: String) : SearchViewState()

    object OnLoadingCategoryFilter :  SearchViewState()
    data class OnSuccessCategoryFilter(val data : SearchCategoryFilterResponse) : SearchViewState()
    data class OnFailCategoryFilter(val message: String) : SearchViewState()


    object OnLoadingSearchRestaurant :  SearchViewState()
    data class OnSuccessSearchRestaurant(val data : SearchRestaurantResponse) : SearchViewState()
    data class OnFailSearchRestaurant(val message : String) : SearchViewState()

    object OnLoadingFilterRestaurant :  SearchViewState()
    data class OnSuccessFilterRestaurant(val data : FilterRestaurantResponse) : SearchViewState()
    data class OnFailFilterRestaurant(val message: String) : SearchViewState()

    object OnLoadingOperateWishList :  SearchViewState()
    data class OnSuccessOperateWishList(val data : OperateWishListResponse) : SearchViewState()
    data class OnFailOperateWishList(val message: String) : SearchViewState()

    object OnLoadingFilterList : SearchViewState()
    data class OnSuccessFilterList(val data : CategoryListResponse) : SearchViewState()
    data class OnFailFilterList(val message : String) : SearchViewState()
}

sealed class FilterViewState {
    object OnLoadFilter : FilterViewState()
    data class OnSuccessFilter(val data : FilterRestaurantResponse) : FilterViewState()
    data class OnFailFilter(val message : String?) : FilterViewState()
}
