package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.responses.*
import com.joy.fattyfood.domain.responses.SearchCategoryFilterResponse
import retrofit2.Response

interface SearchRepository {

    suspend fun customerSearch(keyword: String, customerId: Int, lat: Double, lng: Double):
        Response<SearchFoodAndRestaurantsResponse>

    suspend fun searchFoods(search_type : String,search_name : String) : Response<SearchFoodResponse>
    suspend fun searchRestaurants(search_type : String,search_name : String) : Response<SearchRestaurantResponse>
    suspend fun filterRestaurantRestaurants(category_list : String,customerId: Int,lat: Double,lng: Double) : Response<FilterRestaurantResponse>

    suspend fun fetchCategoryFilterSearch(language : String) : Response<SearchCategoryFilterResponse>

    suspend fun operateWishList(customer_id: Int,restaurant_id : Int) : Response<OperateWishListResponse>
}
