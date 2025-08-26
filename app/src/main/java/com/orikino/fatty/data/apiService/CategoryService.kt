package com.orikino.fatty.data.apiService

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.utils.helper.ApiRouteConstant
import com.orikino.fatty.utils.PreferenceUtils
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CategoryService {
    @POST(ApiRouteConstant.routeCategoryList)
    suspend fun fetchCategoryList(): Response<CategoryListResponse>

    @POST(ApiRouteConstant.routeCategoryByCategoryID)
    @FormUrlEncoded
    suspend fun fetchCategoryByCategoryId(
        @Field("category_id") category_id : Int,
        @Field("customer_id") customer_id: Int = PreferenceUtils.readUserVO().customer_id ?: 0,
        @Field("latitude") latitude : Double = PreferenceUtils.readUserVO().latitude?:0.0,
        @Field("longitude") longitude : Double = PreferenceUtils.readUserVO().longitude?:0.0

    ) : Response<CategoryByCategoryIdResponse>


}