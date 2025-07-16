package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.responses.*
import retrofit2.Response


interface AuthRepository {

    suspend fun requestPhoneOTP(phone: String) : Response<RequestPhoneOtpResponse>

    suspend fun resentRequestOTP(phone: String) : Response<RequestPhoneOtpResponse>

    suspend fun verifyOtp(customerPhone : String, fcmToken : String, osType : Int,otp : Int) : Response<LoginResponse>


}