package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.model.CustomerVO
import com.joy.fattyfood.domain.responses.*
import retrofit2.Response

interface AboutRepository {

    suspend fun fetchAbout(): Response<AboutAppResponse>

    suspend fun fetchTutorial(): Response<TutorialResponse>

    suspend fun fetchPrivacyPolicy(): Response<PrivacyPolicyResponse>

    suspend fun fetchTermCondition(): Response<TermConditionResponse>

    suspend fun fetchCurrency() : Response<CurrencyResponse>

    suspend fun fetchHelpCenter(): Response<AppHelpCenterResponse>

    suspend fun fetchLogout(customerId : Int) : Response<LogoutResponse>

    suspend fun updateProfile(device_id : String,customerVO: CustomerVO) : Response<UpdateUserInfoResponse>
}
