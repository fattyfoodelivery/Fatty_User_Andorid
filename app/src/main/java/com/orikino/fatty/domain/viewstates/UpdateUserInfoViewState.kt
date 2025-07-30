package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*

sealed class UpdateUserInfoViewState{
    object OnLoadingUpdateUserInfo :  UpdateUserInfoViewState()
    data class OnSuccessUpdateUserInfo(val data : UpdateUserInfoResponse) : UpdateUserInfoViewState()
    data class OnFailUpdateUserInfo(val message: String?) : UpdateUserInfoViewState()
}