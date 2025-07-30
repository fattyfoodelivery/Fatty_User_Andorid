package com.orikino.fatty.data.apiService

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.domain.model.CustomerVO
import com.orikino.fatty.utils.helper.ApiRouteConstant
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
