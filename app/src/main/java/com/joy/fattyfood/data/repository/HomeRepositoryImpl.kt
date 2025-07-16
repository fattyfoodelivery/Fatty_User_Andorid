package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.responses.*
import com.joy.fattyfood.data.apiService.HomeService
import com.joy.fattyfood.domain.responses.TopRelatedCategoryResponse
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
            /*Flow<HomeResponse> {
        var response = HomeResponse()
        return flow {
            withContext(Dispatchers.IO) {
                try {
                    response = homeService.fetchHome(
                        customer_id, stateName, latitude, longitude
                    )
                } catch (e: Throwable) {
                    when (e) {
                        is NetworkException -> throw NetworkException("Connection Issue")
                        is HttpException -> throw when {
                            e.response()
                                ?.code() == 500 -> AppException("Server Error")
                            e.response()
                                ?.code() == 403 -> AppException("DENIED")
                            e.response()?.code() == 406 -> AppException("Another Login")
                            else -> AppException("Something Wrong")
                        }
                        else -> throw AppException("Failed")
                    }
                }
            }
            emit(response)
        }
    }*/

    override suspend fun updateUserInfo(
        customer_id : Int,latitude : Double,longitude : Double
    ): Response<UpdateUserInfoResponse> = homeService.updateUserLocation(customer_id, latitude, longitude)

    override suspend fun fetchTopRelatedCategory(
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ): Response<TopRelatedCategoryResponse> = homeService.fetchTopRelatedCategory(customer_id, latitude, longitude)

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