package com.orikino.fatty.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orikino.fatty.domain.viewstates.UpdateUserInfoViewState
import com.orikino.fatty.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    var viewState: MutableLiveData<UpdateUserInfoViewState> = MutableLiveData()
    var userName: String = ""
    var image: String = ""

    fun updateUserInfo(
        deviceToken: String,
        customerId: Int,
        customerName: String,
        customerPhone: String,
        image: String,
        fcmToken: String
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            viewState.postValue(UpdateUserInfoViewState.OnLoadingUpdateUserInfo)
            try {
                val response = userRepository.updateUserInfo(
                    device_id = deviceToken,
                    customer_id = customerId,
                    customer_name = customerName,
                    customer_phone = customerPhone,
                    image = image,
                    fcm_token =  fcmToken
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(UpdateUserInfoViewState.OnSuccessUpdateUserInfo(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(UpdateUserInfoViewState.OnFailUpdateUserInfo("Server Issue")) }
                        403 -> { viewState.postValue(UpdateUserInfoViewState.OnFailUpdateUserInfo("Denied")) }
                        406 -> { viewState.postValue(UpdateUserInfoViewState.OnFailUpdateUserInfo("Another Login")) }
                    }
                }
            }catch (e : Exception) {
                viewState.postValue(UpdateUserInfoViewState.OnFailUpdateUserInfo("Connection Issue"))
            }
        }

    }
}