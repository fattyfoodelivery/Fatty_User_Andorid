package com.orikino.fatty.data.repository

import com.orikino.fatty.domain.responses.*
import retrofit2.Response

interface CategoryRepository {
    suspend fun fetchCategoryList() : Response<CategoryListResponse>
    suspend fun fetchCategoryByCategoryId(categoryId : Int, cutomerId : Int, latitude : Double, longitude : Double) : Response<CategoryByCategoryIdResponse>
}