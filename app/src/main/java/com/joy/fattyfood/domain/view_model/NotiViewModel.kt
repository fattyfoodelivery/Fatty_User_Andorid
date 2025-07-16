package com.joy.fattyfood.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joy.fattyfood.data.repository.NotiRepository
import com.joy.fattyfood.domain.model.*
import com.joy.fattyfood.domain.viewstates.InboxViewState
import com.joy.fattyfood.utils.Constants
import com.joy.fattyfood.utils.PreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotiViewModel @Inject constructor(
    private val repository : NotiRepository
) : ViewModel() {

    var viewState: MutableLiveData<InboxViewState> = MutableLiveData()

    var inboxLiveDataList : MutableLiveData<MutableList<InboxVO>> = MutableLiveData()
    var notificationTypeListLiveData : MutableLiveData<MutableList<NotiTypeVO>> = MutableLiveData()
    var activeParcelListLiveData : MutableLiveData<MutableList<ActiveOrderVO>> = MutableLiveData()
    var pagingList  = mutableListOf<InboxVO>()
    var notiType = 0
    var totalPage = 1
    var currentPage = 1
    var isLoading = false


    fun fetchUserNotiList(page : Int,filter_date : String) {
        viewModelScope.launch {
            try {
                val response = repository.fetchUserNotiList(page,filter_date,PreferenceUtils.readUserVO()?.customer_id ?: 0
                )
                viewState.postValue(InboxViewState.OnLoadingUserNotiList)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(InboxViewState.OnSuccesUserNotiList(it))
                    }
                } else {
                    when(response.code()) {
                        500 -> { viewState.postValue(InboxViewState.OnFailUserNotiList(Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(InboxViewState.OnFailUserNotiList(Constants.DENIED)) }
                        406 -> { viewState.postValue(InboxViewState.OnFailUserNotiList(Constants.ANOTHER_LOGIN)) }
                    }
                }
            }catch (e : Exception) {
                viewState.postValue(InboxViewState.OnFailUserNotiList(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun fetchSystemNot(page : Int,filter_date: String) {
        viewModelScope.launch (Dispatchers.IO){
            try {
                var response = repository.fetchSystemNotiList(page,filter_date,
                    PreferenceUtils.readUserVO()?.customer_id ?: 0)
                viewState.postValue(InboxViewState.OnLoadingSystemNotiList)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(InboxViewState.OnSuccessSystemNotiList(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(InboxViewState.OnFailSystemNotiList(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(InboxViewState.OnFailSystemNotiList(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(InboxViewState.OnFailSystemNotiList(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(InboxViewState.OnFailSystemNotiList(Constants.CONNECTION_ISSUE))
            }
        }
    }


    fun readNoti(noti_id : Int,noti_type : String,user_id : Int) {
        viewModelScope.launch (Dispatchers.IO){
            viewState.postValue(InboxViewState.OnLoadingReadNoti)
            try {
                val response = repository.readNoti(noti_id,noti_type,user_id)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(InboxViewState.OnSuccessReadNoti(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> { viewState.postValue(InboxViewState.OnFailReadNoti(Constants.SERVER_ISSUE)) }
                        403 -> { viewState.postValue(InboxViewState.OnFailReadNoti(Constants.DENIED)) }
                        406 -> { viewState.postValue(InboxViewState.OnFailReadNoti(Constants.ANOTHER_LOGIN)) }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(InboxViewState.OnFailReadNoti(Constants.CONNECTION_ISSUE))
            }
        }
    }


    /*fun fetchInboxNoti() {
        viewModelScope.launch (Dispatchers.IO){
            try {
                var response = repository.fetchInboxNotiList(
                    PreferenceUtils.readUserVO()?.customer_id ?: 0,
                    "","",notiType,currentPage)
                viewState.postValue(com.solinx_tech.fattyfood.domain.viewstates.InboxViewState.OnLoadingInbox)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(com.solinx_tech.fattyfood.domain.viewstates.InboxViewState.OnSuccessInbox(it))
                    } ?: run {
                        viewState.postValue(com.solinx_tech.fattyfood.domain.viewstates.InboxViewState.OnFailInbox(Constants.FAILED))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(com.solinx_tech.fattyfood.domain.viewstates.InboxViewState.OnFailInbox(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(com.solinx_tech.fattyfood.domain.viewstates.InboxViewState.OnFailInbox(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(com.solinx_tech.fattyfood.domain.viewstates.InboxViewState.OnFailInbox(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(com.solinx_tech.fattyfood.domain.viewstates.InboxViewState.OnFailInbox(Constants.CONNECTION_ISSUE))
            }
        }
    }


    fun fetchNotiType() {
        viewModelScope.launch (Dispatchers.IO){
            try {
                var response = repository.fetchNotiType()
                viewState.postValue(InboxViewState.OnLoadingNotiType)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(com.solinx_tech.fattyfood.domain.viewstates.InboxViewState.OnSuccessNotificationType(it))
                    } ?: run {
                        viewState.postValue(com.solinx_tech.fattyfood.domain.viewstates.InboxViewState.OnFailNotificationType(Constants.FAILED))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(com.solinx_tech.fattyfood.domain.viewstates.InboxViewState.OnFailNotificationType(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(InboxViewState.OnFailNotificationType(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(InboxViewState.OnFailNotificationType(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(InboxViewState.OnFailNotificationType(Constants.CONNECTION_ISSUE))
            }
        }
    }*/
}