package com.orikino.fatty.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orikino.fatty.data.repository.AboutRepository
import com.orikino.fatty.domain.model.CurrencyVO
import com.orikino.fatty.domain.model.CustomerVO
import com.orikino.fatty.utils.Constants
import com.orikino.fatty.domain.viewstates.AboutViewState
import com.orikino.fatty.utils.PreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val aboutRepository: AboutRepository
) : ViewModel() {

    var viewState: MutableLiveData<AboutViewState> = MutableLiveData()
    var userName: String = ""
    var image: String =""

    fun fetchAboutApp() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = aboutRepository.fetchAbout()
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(AboutViewState.OnSuccessAbout(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(AboutViewState.OnFailAbout(Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(AboutViewState.OnFailAbout(Constants.DENIED)) }
                        406 -> { viewState.postValue(AboutViewState.OnFailAbout(Constants.ANOTHER_LOGIN)) }
                    }
                }
            }catch (e : Exception) {
                viewState.postValue(AboutViewState.OnFailAbout(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun fetchTermAndCondition() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = aboutRepository.fetchTermCondition()
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(AboutViewState.OnSuccessTermCondition(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(AboutViewState.OnFailTermCondition(Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(AboutViewState.OnFailTermCondition(Constants.DENIED)) }
                        406 -> { viewState.postValue(AboutViewState.OnFailTermCondition(Constants.ANOTHER_LOGIN)) }
                    }
                }
            }catch (e : Exception) {
                viewState.postValue(AboutViewState.OnFailAbout(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun fetchPrivacy() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = aboutRepository.fetchPrivacyPolicy()
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(AboutViewState.OnSuccessPrivacyPolicy(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(AboutViewState.OnFailPrivacyPolicy(Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(AboutViewState.OnFailPrivacyPolicy(Constants.DENIED)) }
                        406 -> { viewState.postValue(AboutViewState.OnFailPrivacyPolicy(Constants.ANOTHER_LOGIN)) }
                    }
                }
            }catch (e : Exception) {
                viewState.postValue(AboutViewState.OnFailPrivacyPolicy(Constants.CONNECTION_ISSUE))
            }
        }

    }

    var currencyVO : CurrencyVO = CurrencyVO()

    var currCurrency : Boolean = false
    fun fetchCurrency() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = aboutRepository.fetchCurrency()
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(AboutViewState.OnSuccessCurrency(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(AboutViewState.OnFailCurrency(Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(AboutViewState.OnFailCurrency(Constants.DENIED)) }
                        406 -> { viewState.postValue(AboutViewState.OnFailCurrency(Constants.ANOTHER_LOGIN)) }
                    }
                }
            }catch (e : Exception) {
                viewState.postValue(AboutViewState.OnFailCurrency(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun fetchHelpCenter() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = aboutRepository.fetchHelpCenter()
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(AboutViewState.OnSuccessHelpCenter(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(AboutViewState.OnFailHelpCenter(Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(AboutViewState.OnFailHelpCenter(Constants.DENIED)) }
                        406 -> { viewState.postValue(AboutViewState.OnFailHelpCenter(Constants.ANOTHER_LOGIN)) }
                    }
                }
            }catch (e : Exception) {
                viewState.postValue(AboutViewState.OnFailHelpCenter(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun fetchLogout(cusId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = aboutRepository.fetchLogout(cusId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(AboutViewState.OnSuccessLogout(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(AboutViewState.OnFailLogout(Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(AboutViewState.OnFailLogout(Constants.DENIED)) }
                        406 -> { viewState.postValue(AboutViewState.OnFailLogout(Constants.ANOTHER_LOGIN)) }
                    }
                }
            }catch (e : Exception) {
                viewState.postValue(AboutViewState.OnFailLogout(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun fetchTutorialApp() {
        viewModelScope.launch(Dispatchers.IO) {
            viewState.postValue(AboutViewState.OnLoadingTutorial)
            try {
                var response = aboutRepository.fetchTutorial()
                if (response.isSuccessful) {
                    response.body()?.let { viewState.postValue(AboutViewState.OnSuccessTutorial(it)) }
                } else {
                    when(response.code()) {
                        500 -> viewState.postValue(AboutViewState.OnFailTutorial("Server Issue"))
                        406 -> {}
                        403 -> {}
                    }
                }
            } catch (e : Exception) {
                e.printStackTrace()
                viewState.postValue(AboutViewState.OnFailTutorial(e.localizedMessage))
            }
        }
    }

    fun updateCusName(device_id : String,customerVO: CustomerVO) {
        viewModelScope.launch(Dispatchers.IO) {
            viewState.postValue(AboutViewState.OnLoadingUpdateName)
            try {
                var response = aboutRepository.updateProfile(device_id,customerVO)
                if (response.isSuccessful) {
                    response.body()?.let { viewState.postValue(AboutViewState.OnSuccessUpdateName(it)) }
                } else {
                    when(response.code()) {
                        500 -> viewState.postValue(AboutViewState.OnFailUpdateName("Server Issue"))
                        406 -> viewState.postValue(AboutViewState.OnFailUpdateName("Another Login"))
                        403 -> viewState.postValue(AboutViewState.OnFailUpdateName("Denied"))
                    }
                }
            } catch (e : Exception) {
                e.printStackTrace()
                viewState.postValue(AboutViewState.OnFailUpdateName(e.localizedMessage))
            }
        }
    }


    fun updateProfile(
        device_id : String,
        customer_id: Int,
        customer_name: String,
        customer_phone: String,
        image: String,
        fcm_token : String) {
        viewModelScope.launch(Dispatchers.IO) {
            viewState.postValue(AboutViewState.OnLoadUpdateProfile)
            try {
                var response = aboutRepository.updateProfile(device_id,CustomerVO(customer_id = customer_id, customer_type_id = PreferenceUtils.readUserVO().customer_type_id,is_restricted = PreferenceUtils.readUserVO().is_restricted,customer_name=customer_name,customer_phone,image = image))
                if (response.isSuccessful) {
                    response.body()?.let { viewState.postValue(AboutViewState.OnSuccessUpdateProfile(it)) }
                } else {
                    when(response.code()) {
                        500 -> viewState.postValue(AboutViewState.OnFailUpdateProfile("Server Issue"))
                        406 -> viewState.postValue(AboutViewState.OnFailUpdateProfile("Another Login"))
                        403 -> viewState.postValue(AboutViewState.OnFailUpdateProfile("Denied"))
                    }
                }
            } catch (e : Exception) {
                e.printStackTrace()
                viewState.postValue(AboutViewState.OnFailUpdateProfile(e.localizedMessage))
            }
        }
    }


}