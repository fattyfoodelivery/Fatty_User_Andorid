package com.orikino.fatty.data.apiService


import com.orikino.fatty.domain.model.CustomerVO
import com.orikino.fatty.utils.helper.ApiRouteConstant
import com.orikino.fatty.domain.responses.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AboutService {

    @GET(ApiRouteConstant.routeAboutApp)
    suspend fun fetchAbout(): Response<AboutAppResponse>

    @GET(ApiRouteConstant.routeDeleteAccount)
    suspend fun deleteAccount() : Response<DeleteAccountResponse>

    @GET(ApiRouteConstant.routeAppTutorial)
    suspend fun fetchTutorial(): Response<TutorialResponse>

    @GET(ApiRouteConstant.routePrivacyPolicy)
    suspend fun fetchPrivacyPolicy(): Response<PrivacyPolicyResponse>

    @GET(ApiRouteConstant.routeTermAndCondition)
    suspend fun fetchTermCondition(): Response<TermConditionResponse>

    @GET(ApiRouteConstant.routeHelpCenter)
    suspend fun fetchHelpCenter(): Response<AppHelpCenterResponse>

    @GET(ApiRouteConstant.routeCurrencyList)
    suspend fun fetchCurrency() : Response<CurrencyResponse>

    @POST(ApiRouteConstant.routeLogoutApp)
    @FormUrlEncoded
    suspend fun logout(
        @Field("customer_id") customer_id : Int
    ) : Response<LogoutResponse>

    @POST(ApiRouteConstant.routeUpdateCustomerInfo)
    suspend fun updateUserInto(
        @Header("device_id")device_id: String,
        @Body customerVO: CustomerVO
    ): Response<UpdateUserInfoResponse>

    @POST(ApiRouteConstant.routeVersionCheck)
    suspend fun versionUpdate(
    ) : Response<VersionUpdateResponse>
}
