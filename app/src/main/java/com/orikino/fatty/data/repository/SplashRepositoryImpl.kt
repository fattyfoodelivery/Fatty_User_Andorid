package com.orikino.fatty.data.repository

import com.orikino.fatty.data.apiService.SplashService
import com.orikino.fatty.domain.responses.OnBoardingResponse
import retrofit2.Response
import javax.inject.Inject

class SplashRepositoryImpl @Inject constructor(
    private val service: SplashService
) : SplashRepository {

    override suspend fun onBoardAds(): Response<OnBoardingResponse> = service.onBoardAds()

    /*override suspend fun onBoardAds(): Flow<OnBoardAdResponse> {
        var response
        return flow {
            withContext(Dispatchers.IO) {
                try {
                    var response = service.onBoardAds()
                } catch (e: Throwable) {
                    when (e) {
                        is NetworkException -> throw NetworkException("Connection Issue")
                        is HttpException -> throw when {
                            e.response()
                                ?.code() == 500 -> AppDefaultException("Server Error")
                            e.response()
                                ?.code() == 403 -> AppDefaultException("DENIED")
                            e.response()?.code() == 406 -> AppDefaultException("Another Login")
                            else -> AppDefaultException("Something Wrong")
                        }
                        else -> throw AppDefaultException("Failed")
                    }
                }
            }
            emit(response)
        }
    }*/

    /*var response = AboutResponse()
        return flow {
            withContext(Dispatchers.IO) {
                try {
                    response = api.fetchAbout()
                } catch (e: Throwable) {
                    when (e) {
                        is NetworkException -> throw NetworkException("Connection Issue")
                        is HttpException -> throw when {
                            e.response()
                                ?.code() == 500 -> AppException("Server Error")
                            e.response()
                                ?.code() == 403 -> AppException("DENIED")
                            e.response()?.code() == 406 -> AppException("Another Login")
                            else -> AppException("Something Wrong")
                        }
                        else -> throw AppException("Failed")
                    }
                }
            }
            emit(response)
        }*/
    /*{
       return try {
            service.onBoardAds()
        }catch (e : Exception) {
            when (e) {
                is NetworkException -> throw NetworkException("Connection Issue")
                is HttpException -> throw when {
                    e.response()
                        ?.code() == 500 -> AppException("Server Error")
                    e.response()
                        ?.code() == 403 -> AppException("DENIED")
                    e.response()?.code() == 406 -> AppException("Another Login")
                    else -> AppException("Something Wrong")
                }
                else -> throw AppException("Failed")
            }
        }

    }*/// = service.onBoardAds()

        /*var response = OnBoardAdsResponse()
        return flow {
            withContext(Dispatchers.IO) {
                try {
                    response = service.onBoardAds()
                } catch (e: Throwable) {
                    when (e) {
                        is NetworkException -> throw NetworkException("Connection Issue")
                        is HttpException -> throw when {
                            e.response()
                                ?.code() == 500 -> AppException("Server Error")
                            e.response()
                                ?.code() == 403 -> AppException("DENIED")
                            e.response()?.code() == 406 -> AppException("Another Login")
                            else -> AppException("Something Wrong")
                        }
                        else -> throw AppException("Failed")
                    }
                }
            }
            emit(response)
        }*/


    /*override suspend fun versionUpdate(versionCode: Int): Response<VersionUpdateResponse> = service.versionUpdate(version_code = versionCode)*/
    /* {
        var response = VersionUpdateResponse()
        return flow {
            withContext(Dispatchers.IO) {
                try {
                    response = service.versionUpdate(versionCode)
                } catch (e: Throwable) {
                    when (e) {
                        is NetworkException -> throw NetworkException("Connection Issue")
                        is HttpException -> throw when {
                            e.response()
                                ?.code() == 500 -> AppException("Server Error")
                            e.response()
                                ?.code() == 403 -> AppException("DENIED")
                            e.response()?.code() == 406 -> AppException("Another Login")
                            else -> AppException("Something Wrong")
                        }
                        else -> throw AppException("Failed")
                    }
                }
            }
            emit(response)
        }
    }*/

    /*override suspend fun adsEngagement(adsId: Int): Response<AdsEngagementResponse>  = service.adsEngagement(merchant_ads_id = adsId)*/
    /*{
        var response = AdsEngagementResponse()
        return flow {
            withContext(Dispatchers.IO) {
                try {


                    response = service.adsEngagement(adsId)
                } catch (e: Throwable) {
                    when (e) {
                        is NetworkException -> throw NetworkException("Connection Issue")
                        is HttpException -> throw when {
                            e.response()
                                ?.code() == 500 -> AppException("Server Error")
                            e.response()
                                ?.code() == 403 -> AppException("DENIED")
                            e.response()?.code() == 406 -> AppException("Another Login")
                            else -> AppException("Something Wrong")
                        }
                        else -> throw AppException("Failed")
                    }
                }
            }
            emit(response)
        }
    }*/
    /*override suspend fun onBoardAds(): Flow<OnBoardAdResponse> {
        TODO("Not yet implemented")
    }*/
}