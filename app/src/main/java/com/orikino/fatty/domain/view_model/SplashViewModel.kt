package com.orikino.fatty.domain.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orikino.fatty.data.repository.SplashRepositoryImpl
import com.orikino.fatty.domain.viewstates.SplashViewState
import com.orikino.fatty.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: SplashRepositoryImpl
) : ViewModel() {

    var viewState: MutableLiveData<SplashViewState> = MutableLiveData()


    fun onBoardingAd() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.onBoardAds()
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(SplashViewState.OnBoardAdSuccess(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(SplashViewState.OnBoardAdFail(Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(SplashViewState.OnBoardAdFail(Constants.DENIED)) }
                        406 -> { viewState.postValue(SplashViewState.OnBoardAdFail(Constants.ANOTHER_LOGIN)) }
                    }
                }
            }catch (e : Exception) {
                Log.d("SPLASHSHSHSHH", "onBoardingAd: ${e.message} adnddd ${e.localizedMessage}")
                viewState.postValue(SplashViewState.OnBoardAdFail(Constants.CONNECTION_ISSUE))
            }
        }
    }

    /*fun onBoard() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.onBoardAds()
                .onStart { viewState.postValue(SplashViewState.OnLoadingOnBoardAds) }
                .catch {
                    viewState.postValue(
                        SplashViewState.OnBoardAdFail(
                            it.localizedMessage ?: "Error"
                        )
                    )
                }
                .collect { viewState.postValue(SplashViewState.OnBoardAdSuccess(it.data)) }
        }
    }*/
}


    /*fun onBoardingAd() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.onBoardAds()
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(SplashViewState.OnBoardAdSuccess(it))
                    } ?: run {
                        viewState.postValue(SplashViewState.OnBoardAdFail(Constants.FAILED))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(SplashViewState.OnBoardAdFail(Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(SplashViewState.OnBoardAdFail(Constants.DENIED)) }
                        406 -> { viewState.postValue(SplashViewState.OnBoardAdFail(Constants.ANOTHER_LOGIN)) }
                    }
                }
            }catch (e : Exception) {
                viewState.postValue(SplashViewState.OnBoardAdFail(Constants.CONNECTION_ISSUE))
            }
        }
    }
}*/