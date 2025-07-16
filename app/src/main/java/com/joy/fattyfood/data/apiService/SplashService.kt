package com.joy.fattyfood.data.apiService

import com.joy.fattyfood.domain.responses.*
import com.joy.fattyfood.utils.helper.ApiRouteConstant
import retrofit2.Response
import retrofit2.http.POST

interface SplashService {

    @POST(ApiRouteConstant.routeOnBoarding)
    suspend fun onBoardAds() : Response<OnBoardingResponse>

    /*@POST(ApiRouteConstant.routeVersionCheck)
    @FormUrlEncoded
    suspend fun versionUpdate(
        @Field("version_code") version_code : Int
    ) : Response<VersionUpdateResponse>

    @POST(ApiRouteConstant.routeAdsEngagement)
    @FormUrlEncoded
    suspend fun adsEngagement(
        @Field("merchant_ads_id") merchant_ads_id : Int
    ) : Response<AdsEngagementResponse>*/


}