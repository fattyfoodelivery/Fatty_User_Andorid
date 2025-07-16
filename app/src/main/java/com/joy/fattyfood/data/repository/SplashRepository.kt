package com.joy.fattyfood.data.repository

import com.joy.fattyfood.domain.responses.*
import retrofit2.Response

interface SplashRepository {

    suspend fun onBoardAds(): Response<OnBoardingResponse>

    /*suspend fun versionUpdate(versionCode: Int): Response<VersionUpdateResponse>

    suspend fun adsEngagement(adsId: Int): Response<AdsEngagementResponse>*/
}
