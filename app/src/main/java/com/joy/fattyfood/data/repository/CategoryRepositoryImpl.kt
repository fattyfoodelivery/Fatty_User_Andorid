package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.responses.*
import com.joy.fattyfood.data.apiService.CategoryService
import retrofit2.Response
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryService: CategoryService
) : CategoryRepository{
    override suspend fun fetchCategoryList(): Response<CategoryListResponse> = categoryService.fetchCategoryList()
    override suspend fun fetchCategoryByCategoryId(categoryId: Int): Response<CategoryByCategoryIdResponse> = categoryService.fetchCategoryByCategoryId(categoryId)
}