package com.joy.fattyfood.data.apiService

import com.joy.fattyfood.domain.responses.*
import com.joy.fattyfood.utils.helper.ApiRouteConstant
import com.joy.fattyfood.utils.PreferenceUtils
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SearchService {

    @POST(ApiRouteConstant.routeSearchFoodAndRestaurant)
    @FormUrlEncoded
    suspend fun searchFoodAndRestaurants(
        @Field(value = "search_name") search_name: String,
        @Field(value = "customer_id") customer_id: Int,
        @Field(value = "latitude") latitude: Double,
        @Field(value = "longitude") longitude: Double
    ): Response<SearchFoodAndRestaurantsResponse>


    @POST(ApiRouteConstant.routeSearchFoodAndRestaurant)
    @FormUrlEncoded
    suspend fun searchFood(
        @Field("search_type") search_type: String,
        @Field("search_name") search_name: String,
        @Field("customer_id") customer_id : Int = PreferenceUtils.readUserVO()?.customer_id ?: 0,
        @Field("latitude") latitude : Double = PreferenceUtils.readUserVO()?.latitude ?: 0.0,
        @Field("longitude") longitude : Double = PreferenceUtils.readUserVO()?.longitude ?: 0.0
    ): Response<SearchFoodResponse>

    @POST(ApiRouteConstant.routeSearchFoodAndRestaurant)
    @FormUrlEncoded
    suspend fun searchRestaurant(
        @Field("search_type") search_type: String,
        @Field("search_name") search_name: String,
        @Field("customer_id") customer_id : Int = PreferenceUtils.readUserVO().customer_id ?: 0,
        @Field("latitude") latitude : Double = PreferenceUtils.readUserVO().latitude ?: 0.0,
        @Field("longitude") longitude : Double = PreferenceUtils.readUserVO().longitude ?: 0.0
    ): Response<SearchRestaurantResponse>

    @POST(ApiRouteConstant.routeFilterRestaurant)
    @FormUrlEncoded
    suspend fun filterRestaurantRestaurant(
        @Field("category_list") category_list: String,
        @Field("customer_id") customer_id : Int,
        @Field("latitude") latitude : Double,
        @Field("longitude") longitude : Double
    ): Response<FilterRestaurantResponse>

    @POST(ApiRouteConstant.routeCategoryFilterSearch)
    @FormUrlEncoded
    suspend fun fetchSearchCategoryFilter(@Field("language") language: String) : Response<SearchCategoryFilterResponse>

    @POST(ApiRouteConstant.routeCustomerWishList)
    @FormUrlEncoded
    suspend fun operateWishList(
        @Field("customer_id") customer_id: Int,
        @Field("restaurant_id") restaurant_id: Int
    ): Response<OperateWishListResponse>
}
