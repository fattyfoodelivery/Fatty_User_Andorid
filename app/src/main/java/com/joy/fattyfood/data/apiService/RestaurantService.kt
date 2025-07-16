package com.joy.fattyfood.data.apiService

import com.joy.fattyfood.domain.responses.*
import com.joy.fattyfood.utils.helper.ApiRouteConstant
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RestaurantService {

    //@POST("api/v1/fatty/202221/lashio/main/admin/click/menu/data")
    //@POST(ApiRouteConstant.routeFoodMenuByRestaurant)
    /*@FormUrlEncoded
    suspend fun fetchFoodMenuByRestaurant(
        @Field("restaurant_id") restaurant_id : Int,
        @Field("customer_id") customer_id: Int,
        @Field("latitude") latitude : Double = Preference.readUserVO().latitude?:0.0,
        @Field("longitude") longitude : Double = Preference.readUserVO().longitude?:0.0
    ) : FoodMenuByRestaurantResponse*/

    //@POST("api/v1/fatty/202221/lashio/main/admin/customers/wishlists")
    @POST(ApiRouteConstant.routeCustomerWishList)
    @FormUrlEncoded
    suspend fun operateWishList(
        @Field("customer_id") customer_id: Int ,
        @Field("restaurant_id") restaurant_id: Int
    ): Response<OperateWishListResponse>

    @POST("newpns/api/v1/fatty/202221/lashio/main/admin/click/restaurant/data")
    @FormUrlEncoded
    suspend fun fetchRestaurantDetail(@Field("restaurant_id") restaurant_id : Int) : Response<RestaurantDetailResponse>

    @POST(ApiRouteConstant.routeFoodMenuByRestaurant)
    @FormUrlEncoded
    suspend fun fetchFoodMenuByRestaurant(
        @Field("restaurant_id") restaurant_id : Int,
        @Field("customer_id") customer_id: Int,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double
    ): Response<FoodMenuByRestaurantResponse>

    /*RestaurantFoodResponse*/
    /*@POST(ApiRouteConstant.routeRestaurantFoodTypeByMenuID)
    @FormUrlEncoded
    suspend fun fetchRestaurantFoodTypeByMenuId(@Field("food_menu_id") food_menu_id : Int) : Response<RestaurantFoodResponse>*/

}