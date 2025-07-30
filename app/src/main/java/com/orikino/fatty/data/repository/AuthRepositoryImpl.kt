package com.orikino.fatty.data.repository


import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.data.apiService.AuthService
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService
) : AuthRepository {

    override suspend fun requestPhoneOTP(phone: String): Response<RequestPhoneOtpResponse> = service.requestPhoneOTP(phone)

    override suspend fun resentRequestOTP(phone: String): Response<RequestPhoneOtpResponse> = service.resendPhoneOTP(phone)
    override suspend fun verifyOtp(
        customerPhone: String,
        fcmToken: String,
        osType: Int,
        otp : Int
    ): Response<LoginResponse>  = service.verifyOtp(customerPhone,fcmToken,osType,otp)


}