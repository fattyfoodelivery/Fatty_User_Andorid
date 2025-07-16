package com.joy.fattyfood.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joy.fattyfood.data.repository.MainRepository
import com.joy.fattyfood.domain.viewstates.MainViewState
import com.joy.fattyfood.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository : MainRepository
) : ViewModel (){


    var viewState: MutableLiveData<MainViewState> = MutableLiveData()

    fun updateUserInfo(
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            val response = repository.updateUserInfo(customer_id, latitude, longitude)
            viewState.postValue(MainViewState.OnLoadingUpdateUserInfo)
            try {
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(MainViewState.OnSuccessUpdateUserInfo(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(MainViewState.OnFailUpdateUserInfo(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(MainViewState.OnFailUpdateUserInfo(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(MainViewState.OnFailUpdateUserInfo(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(MainViewState.OnFailUpdateUserInfo(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun fetchTopRelatedCategory(
        customer_id: Int,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchTopRelatedCategory(customer_id, latitude, longitude)
            try {
                if (response.isSuccessful) {
                    viewState.postValue(MainViewState.OnLoadingTopRelated)
                    response.body()?.let { viewState.postValue(MainViewState.OnSuccessTopRelated(it)) }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(MainViewState.OnFailTopRelated(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(MainViewState.OnFailTopRelated(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(MainViewState.OnFailTopRelated(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(MainViewState.OnFailTopRelated(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun fetchCurrCurrency() {
        viewModelScope.launch {
            try {
                val response = repository.fetchCurrentCurrency()
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(MainViewState.OnSuccessCurrency(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(MainViewState.OnFailCurrency(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(MainViewState.OnFailCurrency(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(MainViewState.OnFailCurrency(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(MainViewState.OnFailCurrency(Constants.CONNECTION_ISSUE))
            }
        }
    }



}