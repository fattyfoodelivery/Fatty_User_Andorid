package com.orikino.fatty.data.apiService

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.utils.helper.ApiRouteConstant
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface SplashService {

    @POST(ApiRouteConstant.routeOnBoarding)
    suspend fun onBoardAds() : Response<OnBoardingResponse>

    @POST(ApiRouteConstant.routeVersionCheck)
    suspend fun versionUpdate(
    ) : Response<VersionUpdateResponse>

   /* @POST(ApiRouteConstant.routeAdsEngagement)
    @FormUrlEncoded
    suspend fun adsEngagement(
        @Field("merchant_ads_id") merchant_ads_id : Int
    ) : Response<AdsEngagementResponse>*/


}