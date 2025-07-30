package com.orikino.fatty.data.repository

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.data.apiService.AboutService
import com.orikino.fatty.domain.model.CustomerVO
import retrofit2.Response
import javax.inject.Inject

class AboutRepositoryImpl @Inject constructor(
    private val service: AboutService
) : AboutRepository {
    override suspend fun fetchAbout(): Response<AboutAppResponse> = service.fetchAbout()

    override suspend fun fetchTutorial(): Response<TutorialResponse> = service.fetchTutorial()

    override suspend fun fetchPrivacyPolicy(): Response<PrivacyPolicyResponse> = service.fetchPrivacyPolicy()
    override suspend fun fetchTermCondition(): Response<TermConditionResponse> = service.fetchTermCondition()
    override suspend fun fetchCurrency(): Response<CurrencyResponse> = service.fetchCurrency()

    override suspend fun fetchHelpCenter(): Response<AppHelpCenterResponse> = service.fetchHelpCenter()
    override suspend fun fetchLogout(customer_id : Int): Response<LogoutResponse> = service.logout(customer_id)
    override suspend fun updateProfile(device_id : String,customerVO: CustomerVO): Response<UpdateUserInfoResponse> = service.updateUserInto(device_id,customerVO)
}
