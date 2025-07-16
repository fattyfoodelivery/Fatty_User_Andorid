package com.joy.fattyfood.data.apiService

import com.joy.fattyfood.domain.responses.*
import com.joy.fattyfood.domain.model.CustomerVO
import com.joy.fattyfood.utils.helper.ApiRouteConstant
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface UserService {

    @POST(ApiRouteConstant.routeUpdateCustomerInfo)
    suspend fun updateUserInto(
        @Header("device_id")device_id: String,
        @Body customerVO: CustomerVO
    ): Response<UpdateUserInfoResponse>


}
