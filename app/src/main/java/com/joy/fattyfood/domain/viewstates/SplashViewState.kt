package com.joy.fattyfood.domain.viewstates

import com.joy.fattyfood.domain.responses.OnBoardingResponse


sealed class SplashViewState {
    object OnLoadingOnBoardAds : SplashViewState()
    data class OnBoardAdSuccess(val data: OnBoardingResponse) : SplashViewState()
    data class OnBoardAdFail(val message: String) : SplashViewState()
}
