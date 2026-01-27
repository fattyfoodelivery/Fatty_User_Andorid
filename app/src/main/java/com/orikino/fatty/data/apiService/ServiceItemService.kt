package com.orikino.fatty.data.apiService

import com.orikino.fatty.domain.responses.ServiceCategoryResponse
import com.orikino.fatty.domain.responses.ServiceItemResponse
import com.orikino.fatty.domain.responses.ShopByCategoryResponse
import com.orikino.fatty.domain.responses.ShopWebLinkResponse
import com.orikino.fatty.utils.helper.ApiRouteConstant
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface ServiceItemService {
    @POST(ApiRouteConstant.routeServiceItem)
    suspend fun fetchServiceItem(
    ) : Response<ServiceItemResponse>

    @POST(ApiRouteConstant.routeServiceCategory)
    suspend fun fetchServiceCategory(
        @Path("service_item_id") service_item_id : Int
    ) : Response<ServiceCategoryResponse>

    @POST(ApiRouteConstant.serviceShopByCategory)
    @FormUrlEncoded
    suspend fun fetchShopsByCategory(
        @Path("service_item_id") service_item_id : Int,
        @Field("sort_by") sort_by : String,
        @Field("latitude") latitude : Double,
        @Field("longitude") longitude : Double,
        @Field("service_category_id") service_category_id : Int?,
        @Field("search_key") search_key : String?,
        @Field("customer_id") customer_id : Int,
        @Field("page") page : Int,
        @Field("page_size") page_size : Int
    ) : Response<ShopByCategoryResponse>

    @POST(ApiRouteConstant.routeGetShopWebLink)
    @FormUrlEncoded
    suspend fun fetchShopWebLink(
        @Field("store_id") store_id : Int,
        @Field("customer_id") customer_id : Int
    ) : Response<ShopWebLinkResponse>
}