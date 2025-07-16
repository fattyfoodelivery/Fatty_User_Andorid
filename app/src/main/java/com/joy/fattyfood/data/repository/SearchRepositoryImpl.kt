package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.responses.*
import com.joy.fattyfood.data.apiService.SearchService
import com.joy.fattyfood.domain.responses.SearchCategoryFilterResponse
import retrofit2.Response
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchService: SearchService
) : SearchRepository {

    override suspend fun customerSearch(
        keyword: String,
        customerId: Int,
        lat: Double,
        lng: Double
    ): Response<SearchFoodAndRestaurantsResponse> {
        return searchService.searchFoodAndRestaurants(keyword, customerId, lat, lng)
    }

    override suspend fun searchFoods(
        search_type: String,
        search_name: String
    ): Response<SearchFoodResponse>  = searchService.searchFood(search_type,search_name)
    override suspend fun searchRestaurants(
        search_type: String,
        search_name: String
    ): Response<SearchRestaurantResponse> = searchService.searchRestaurant(search_type,search_name)

    override suspend fun filterRestaurantRestaurants(category_list: String,customerId: Int,lat: Double,lng: Double):
            Response<FilterRestaurantResponse> = searchService.filterRestaurantRestaurant(category_list,customerId,lat,lng)
    override suspend fun fetchCategoryFilterSearch(language: String): Response<SearchCategoryFilterResponse> = searchService.fetchSearchCategoryFilter(language)
    override suspend fun operateWishList(
        customer_id: Int,
        restaurant_id: Int
    ): Response<OperateWishListResponse> = searchService.operateWishList(customer_id,restaurant_id)
}
