package com.orikino.fatty.data.apiService

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.utils.helper.ApiRouteConstant
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {

    @POST(ApiRouteConstant.routeRequestOTP)
    @FormUrlEncoded
    suspend fun requestPhoneOTP(
        @Field("customer_phone") customer_phone : String,
        @Field("token") token : String ?= "1111"
    ) : Response<RequestPhoneOtpResponse>

    @POST(ApiRouteConstant.routeResendRequestOTP)
    @FormUrlEncoded
    suspend fun resendPhoneOTP(
        @Field("customer_phone") customer_phone: String,
        @Field("token") token : String ?= "1111"
    ): Response<RequestPhoneOtpResponse>


    @POST(ApiRouteConstant.routeCustomerLogin)
    @FormUrlEncoded
    suspend fun customerLogin(
        @Field("customer_phone") customer_phone: String,
        @Field("fcm_token") fcm_token: String,
        @Field("os_type") os_type: Int? = 0
    ): Response<LoginResponse>

    @POST(ApiRouteConstant.routeVerifyOTP)
    @FormUrlEncoded
    suspend fun verifyOtp(
        @Field("customer_phone") customer_phone: String,
        @Field("fcm_token") fcm_token: String,
        @Field("os_type") os_type: Int? = 0,
        @Field("otp") otp: Int,
        @Field("package_name") packageName : String,
        @Field("app_version") versionName : String
    ): Response<LoginResponse>


    /*@POST(ApiRouteConstant.routeVerifyOTP)
    @FormUrlEncoded
    suspend fun verifyOtp(
        @Field("customer_phone") customer_phone: String,
        @Field("fcm_token") fcm_token: String,
        @Field("os_type") os_type: Int? = 0
    ): Response<LoginResponse>*/


}