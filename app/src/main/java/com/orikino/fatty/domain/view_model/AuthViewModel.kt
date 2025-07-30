package com.orikino.fatty.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orikino.fatty.data.repository.AuthRepository
import com.orikino.fatty.domain.viewstates.AuthViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {


    var viewState: MutableLiveData<AuthViewState> = MutableLiveData()


    fun requestPhoneOtp(phone: String) {
        viewModelScope.launch {
            viewState.postValue(AuthViewState.LoadingRequestPhone)
            try {
                val response = authRepository.requestPhoneOTP(phone)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(AuthViewState.OnSuccessRequestPhone(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(AuthViewState.OnFailRequestPhone("Server Issue")) }
                        403 -> { viewState.postValue(AuthViewState.OnFailRequestPhone("DENIED")) }
                        406 -> { viewState.postValue(AuthViewState.OnFailRequestPhone("Another Login")) }
                    }
                }
            }catch (e : Exception) {
                viewState.postValue(AuthViewState.OnFailRequestPhone("Connection Issue"))
            }
        }
    }

    fun resendRequest(phone: String) {
        viewModelScope.launch {
            viewState.postValue(AuthViewState.LoadingResentOtp)
            try {
                val response = authRepository.resentRequestOTP(phone)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(AuthViewState.OnSuccessResentOtp(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(AuthViewState.OnFailRequestResentOtp("Server Issue")) }
                        403 -> { viewState.postValue(AuthViewState.OnFailRequestResentOtp("DENIED")) }
                        406 -> { viewState.postValue(AuthViewState.OnFailRequestResentOtp("Another Login")) }
                    }
                }
            }catch (e : Exception) {
                viewState.postValue(AuthViewState.OnFailRequestResentOtp("Connection Issue"))
            }
        }

    }




}