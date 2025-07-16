package com.joy.fattyfood.domain.viewstates

import com.joy.fattyfood.domain.responses.*

sealed class AuthViewState {
    object LoadingRequestPhone : AuthViewState()
    data class OnSuccessRequestPhone(val data : RequestPhoneOtpResponse) : AuthViewState()
    data class OnFailRequestPhone(val message : String) : AuthViewState()

    object LoadingResentOtp : AuthViewState()
    data class OnSuccessResentOtp(val data : RequestPhoneOtpResponse) : AuthViewState()
    data class OnFailRequestResentOtp(val message: String) : AuthViewState()


}

sealed class VerifyOtpState{
    object OnLoadingVerify : VerifyOtpState()
    data class OnSuccessVerifyOtp(val data : LoginResponse) : VerifyOtpState()
    data class OnFailVerifyOtp(val message: String?) : VerifyOtpState()

    object onLoadExpireRequest : VerifyOtpState()
    data class OnSuccessExpireRequest(val data : RequestPhoneOtpResponse) : VerifyOtpState()
    data class OnFailExpireRequest(val message: String?) : VerifyOtpState()
}