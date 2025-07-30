package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.CurrencyResponse
import com.orikino.fatty.domain.responses.TopRelatedCategoryResponse
import com.orikino.fatty.domain.responses.UpdateUserInfoResponse

sealed class MainViewState {
    object OnLoadingUpdateUserInfo :  MainViewState()
    data class OnSuccessUpdateUserInfo(val data : UpdateUserInfoResponse) : MainViewState()
    data class OnFailUpdateUserInfo(val message: String) : MainViewState()

    object OnLoadingTopRelated : MainViewState()
    data class OnSuccessTopRelated(val data : TopRelatedCategoryResponse) : MainViewState()
    data class OnFailTopRelated(val message: String) : MainViewState()

    object OnLoadingCurrency :  MainViewState()
    data class OnSuccessCurrency(val data : CurrencyResponse) : MainViewState()
    data class OnFailCurrency(val message: String) : MainViewState()
}