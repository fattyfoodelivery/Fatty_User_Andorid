package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.responses.*
import com.joy.fattyfood.data.apiService.UserService
import com.joy.fattyfood.domain.model.CustomerVO
import com.joy.fattyfood.utils.PreferenceUtils
import retrofit2.Response
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService
) : UserRepository{
    override suspend fun updateUserInfo(
        device_id: String,
        customer_id: Int,
        customer_name: String,
        customer_phone: String,
        image: String,
        fcm_token: String
    ): Response<UpdateUserInfoResponse>  = userService.updateUserInto(device_id = device_id, customerVO = CustomerVO(customer_id = customer_id, customer_type_id = PreferenceUtils.readUserVO().customer_type_id,is_restricted = PreferenceUtils.readUserVO().is_restricted,customer_name=customer_name,customer_phone,image = image))

}