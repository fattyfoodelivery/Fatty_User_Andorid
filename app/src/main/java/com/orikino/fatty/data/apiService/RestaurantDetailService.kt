package com.orikino.fatty.data.apiService

import com.orikino.fatty.domain.responses.FoodMenuByRestaurantResponse
import com.orikino.fatty.domain.responses.OperateWishListResponse
import com.orikino.fatty.domain.responses.RestDetailReviewListResponse
import com.orikino.fatty.domain.responses.WishListResponse
import com.orikino.fatty.utils.helper.ApiRouteConstant
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantDetailService {

    @POST(ApiRouteConstant.routeFoodMenuByRestaurant)
    @FormUrlEncoded
    suspend fun fetchFoodMenuByRestaurant(
        @Field("restaurant_id") restaurant_id : Int,
        @Field("customer_id") customer_id: Int,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double
    ): Response<FoodMenuByRestaurantResponse>

    @POST("api/v2/fatty/202221/lashio/main/admin/reviews/restaurant/{restaurnat_id}")
    //@POST(ApiRouteConstant.routeRestaurantDetailReviewList)
    suspend fun restaurantDetailReviewList(
        @Path("restaurnat_id") restaurant_id: Int,
        @Query("page") page : Int,
    ) : Response<RestDetailReviewListResponse>

    @POST(ApiRouteConstant.routeCustomerWishList)
    @FormUrlEncoded
    suspend fun operateWishList(
        @Field("customer_id") customer_id: Int ,
        @Field("restaurant_id") restaurant_id: Int
    ): Response<OperateWishListResponse>

    @POST(ApiRouteConstant.routeWishList)
    @FormUrlEncoded
    suspend fun fetchWishList(
        @Field("customer_id") customer_id: Int,
        @Field("latitude") latitude : Double,
        @Field("longitude") longitude : Double
    ): Response<WishListResponse>
}