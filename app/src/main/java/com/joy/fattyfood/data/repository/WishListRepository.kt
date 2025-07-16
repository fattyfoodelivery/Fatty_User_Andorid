package com.joy.fattyfood.data.repository
import com.joy.fattyfood.domain.responses.*
import retrofit2.Response

interface WishListRepository {

    suspend fun fetchWishList(customer_id: Int,latitude: Double,longitude: Double) : Response<WishListResponse>

    suspend fun operateWishList(customer_id: Int,restaurant_id : Int) : Response<OperateWishListResponse>
}