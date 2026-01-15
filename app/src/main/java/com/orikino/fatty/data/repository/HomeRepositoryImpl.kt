package com.orikino.fatty.data.repository

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.data.apiService.HomeService
import com.orikino.fatty.domain.responses.TopRelatedCategoryResponse
import retrofit2.Response
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeService: HomeService
)  : HomeRepository{


    override suspend fun fetchHome(
        customer_id: Int,
        stateName: String,
        latitude: Double,
        longitude: Double
    ): Response<HomeResponse> = homeService.fetchHome(customer_id,stateName,latitude,longitude)

    override suspend fun fetchServiceItem(): Response<ServiceItemResponse> {
        return homeService.fetchServiceItem()
    }

    override suspend fun updateUserInfo(
        customer_id : Int,latitude : Double,longitude : Double
    ): Response<UpdateUserInfoResponse> = homeService.updateUserLocation(customer_id, latitude, longitude)

    override suspend fun fetchTopRelatedCategory(
        customer_id: Int,
        latitude: Double,
        longitude: Double,
        page : Int,
        pageSize : Int
    ): Response<TopRelatedCategoryResponse> = homeService.fetchTopRelatedCategory(customer_id, latitude, longitude, page, pageSize)

    override suspend fun fetchCurrency(): Response<CurrencyResponse> = homeService.fetchCurrency()
    override suspend fun fetchWishList(
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ): Response<WishListResponse> = homeService.fetchWishList(
        customer_id, latitude, longitude
    )

    override suspend fun operateWishList(
        customer_id: Int,
        restaurant_id: Int
    ): Response<OperateWishListResponse> = homeService.operateWishList(
        customer_id,restaurant_id
    )


}