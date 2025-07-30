package com.orikino.fatty.data.repository

import com.orikino.fatty.domain.responses.*
import retrofit2.Response

interface UserRepository {

    suspend fun updateUserInfo(
        device_id: String,
        customer_id: Int,
        customer_name: String,
        customer_phone: String,
        image: String,
        fcm_token: String
    ): Response<UpdateUserInfoResponse>
}
