package com.joy.fattyfood.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joy.fattyfood.data.repository.AuthRepository
import com.joy.fattyfood.domain.viewstates.VerifyOtpState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val respository : AuthRepository
) : ViewModel() {

    var viewState : MutableLiveData<VerifyOtpState> = MutableLiveData()
    var phoneNo =""

    fun verifyOtp(customerPhone : String, fcmToken : String,osType : Int,otp : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            viewState.postValue(VerifyOtpState.OnLoadingVerify)
            try {
                val response = respository.verifyOtp(customerPhone, fcmToken, osType,otp)
                if (response.isSuccessful) {
                    response.body()?.let { viewState.postValue(VerifyOtpState.OnSuccessVerifyOtp(it)) }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(VerifyOtpState.OnFailVerifyOtp("Server Issue")) }
                        403 -> { viewState.postValue(VerifyOtpState.OnFailVerifyOtp("DENIED")) }
                        406 -> { viewState.postValue(VerifyOtpState.OnFailVerifyOtp("Another Login")) }
                    }
                }
            }catch (e : Exception) {
                e.printStackTrace()
                viewState.postValue(VerifyOtpState.OnFailVerifyOtp(e.localizedMessage))
            }
        }
    }

    fun resentForExpire(phone: String) {
        viewModelScope.launch(Dispatchers.IO) {
            viewState.postValue(VerifyOtpState.onLoadExpireRequest)
            try {
                val response = respository.resentRequestOTP(phone)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(VerifyOtpState.OnSuccessExpireRequest(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(VerifyOtpState.OnFailExpireRequest("Server Issue")) }
                        403 -> { viewState.postValue(VerifyOtpState.OnFailExpireRequest("Denied")) }
                        406 -> { viewState.postValue(VerifyOtpState.OnFailExpireRequest("Another Login")) }
                    }
                }
            }catch (e : Exception) {
                e.printStackTrace()
                viewState.postValue(VerifyOtpState.OnFailExpireRequest(e.localizedMessage))
            }
        }
    }


}