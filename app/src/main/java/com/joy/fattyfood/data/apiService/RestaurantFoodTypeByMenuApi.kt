package com.joy.fattyfood.data.apiService

import com.joy.fattyfood.domain.responses.FoodMenuByRestaurantResponse
import com.joy.fattyfood.domain.responses.OperateWishListResponse
import com.joy.fattyfood.domain.responses.RestaurantFoodResponse
import com.joy.fattyfood.utils.PreferenceUtils
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RestaurantFoodTypeByMenuApi {
    @POST("api/v1/fatty/202221/lashio/main/admin/click/menu/data")
    @FormUrlEncoded
    suspend fun fetchRestaurantFoodTypeByMenuId(@Field("food_menu_id") food_menu_id : Int) : RestaurantFoodResponse
}

interface FoodMenuByRestaurantApi{
    @POST("api/v1/fatty/202221/lashio/main/admin/click/menu/data")
    @FormUrlEncoded
    suspend fun fetchFoodMenuByRestaurant(
        @Field("restaurant_id") restaurant_id : Int,
        @Field("customer_id") customer_id: Int,
        @Field("latitude") latitude : Double = PreferenceUtils.readUserVO()?.latitude?:0.0,
        @Field("longitude") longitude : Double = PreferenceUtils.readUserVO()?.longitude?:0.0
    ) : FoodMenuByRestaurantResponse

    @POST("api/v1/fatty/202221/lashio/main/admin/customers/wishlists")
    @FormUrlEncoded
    suspend fun operateWishList(
        @Field("customer_id") customer_id: Int ,
        @Field("restaurant_id") restaurant_id: Int
    ): OperateWishListResponse
}