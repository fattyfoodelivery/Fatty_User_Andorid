package com.joy.fattyfood.domain.viewstates

import com.joy.fattyfood.domain.responses.*

sealed class UpdateUserInfoViewState{
    object OnLoadingUpdateUserInfo :  UpdateUserInfoViewState()
    data class OnSuccessUpdateUserInfo(val data : UpdateUserInfoResponse) : UpdateUserInfoViewState()
    data class OnFailUpdateUserInfo(val message: String?) : UpdateUserInfoViewState()
}