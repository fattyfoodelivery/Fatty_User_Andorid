package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.OnBoardingResponse


sealed class SplashViewState {
    object OnLoadingOnBoardAds : SplashViewState()
    data class OnBoardAdSuccess(val data: OnBoardingResponse) : SplashViewState()
    data class OnBoardAdFail(val message: String) : SplashViewState()
}
