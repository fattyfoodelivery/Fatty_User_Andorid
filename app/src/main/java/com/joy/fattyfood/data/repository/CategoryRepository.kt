package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.responses.*
import retrofit2.Response

interface CategoryRepository {
    suspend fun fetchCategoryList() : Response<CategoryListResponse>
    suspend fun fetchCategoryByCategoryId(categoryId : Int) : Response<CategoryByCategoryIdResponse>
}