package com.orikino.fatty.data.repository


import com.orikino.fatty.data.apiService.WishListService
import com.orikino.fatty.domain.responses.*
import retrofit2.Response
import javax.inject.Inject

class WishListRepositoryImpl @Inject constructor(
    private val service : WishListService
) : WishListRepository {
    override suspend fun fetchWishList(
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ): Response<WishListResponse> = service.fetchWishList(customer_id, latitude, longitude)

    override suspend fun operateWishList(
        customer_id: Int,
        restaurant_id: Int
    ): Response<OperateWishListResponse> = service.operateWishList(customer_id,restaurant_id)
}