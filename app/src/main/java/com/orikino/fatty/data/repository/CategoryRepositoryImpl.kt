package com.orikino.fatty.data.repository

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.data.apiService.CategoryService
import retrofit2.Response
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryService: CategoryService
) : CategoryRepository{
    override suspend fun fetchCategoryList(): Response<CategoryListResponse> = categoryService.fetchCategoryList()
    override suspend fun fetchCategoryByCategoryId(categoryId : Int, cutomerId : Int, latitude : Double, longitude : Double): Response<CategoryByCategoryIdResponse> = categoryService.fetchCategoryByCategoryId(categoryId, cutomerId, latitude, longitude)
}