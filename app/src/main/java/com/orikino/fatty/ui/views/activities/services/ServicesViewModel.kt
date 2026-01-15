package com.orikino.fatty.ui.views.activities.services

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.orikino.fatty.data.repository.ServiceRepository
import com.orikino.fatty.domain.viewstates.HomeViewState
import com.orikino.fatty.domain.viewstates.ServiceViewState
import com.orikino.fatty.ui.views.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val listPagingState: ServiceShopPagingState
) : BaseViewModel() {
    var viewState: MutableLiveData<ServiceViewState> = MutableLiveData()
    fun fetchServiceCategory(serviceItemId: Int) {
        viewState.postValue(ServiceViewState.OnLoadingServiceCategory)
        viewModelScope.launch {
            try {
                val response = serviceRepository.fetchServiceCategory(serviceItemId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(ServiceViewState.OnSuccessServiceCategory(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(ServiceViewState.OnFailServiceCategory("Server Issue"))
                        }

                        403 -> {
                            viewState.postValue(ServiceViewState.OnFailServiceCategory("Denied"))
                        }

                        406 -> {
                            viewState.postValue(ServiceViewState.OnFailServiceCategory("Another Login"))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewState.postValue(ServiceViewState.OnFailServiceCategory(e.message.toString()))
            }
        }
    }

    fun getTopRelatedCurrentPage() = listPagingState.listCurrentPage

    fun fetchShopByCategory(
        serviceItemID: Int,
        sortBy: String,
        latitude: Double,
        longitude: Double,
        serviceCategoryID: Int?,
        searchKey: String?
    ) {
        viewState.postValue(ServiceViewState.OnLoadingShopByCategory)
        listPagingState.resetListPage()
        viewModelScope.launch {
            try {
                val response = serviceRepository.fetchShopByCategory(
                    serviceItemID, sortBy,
                    latitude, longitude, serviceCategoryID, searchKey, 1, 10
                )
                if (response.isSuccessful) {
                    listPagingState.listMaxPage = response.body()?.meta?.total_pages ?: 0
                    listPagingState.increaseListCurrentPage()
                    response.body()?.let {
                        viewState.postValue(ServiceViewState.OnSuccessShopByCategory(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(ServiceViewState.OnFailShopByCategory("Server Issue"))
                        }

                        403 -> {
                            viewState.postValue(ServiceViewState.OnFailShopByCategory("Denied"))
                        }

                        406 -> {
                            viewState.postValue(ServiceViewState.OnFailShopByCategory("Another Login"))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewState.postValue(ServiceViewState.OnFailShopByCategory(e.message.toString()))
            }
        }

    }

    fun onListEndReachShopByCategory(
        serviceItemID: Int,
        sortBy: String,
        latitude: Double,
        longitude: Double,
        serviceCategoryID: Int?,
        searchKey: String?
    ) {
        viewModelScope.launch {
            viewState.postValue(ServiceViewState.OnLoadingShopByCategory)
            if (listPagingState.shouldNextPageList()) {
                try {
                    val response = serviceRepository.fetchShopByCategory(
                        serviceItemID,
                        sortBy,
                        latitude,
                        longitude,
                        serviceCategoryID,
                        searchKey,
                        listPagingState.listCurrentPage,
                        10
                    )
                    if (response.isSuccessful) {
                        listPagingState.increaseListCurrentPage()
                        response.body()?.let {
                            viewState.postValue(ServiceViewState.OnSuccessShopByCategory(it))
                        }
                    } else {
                        when (response.code()) {
                            500 -> {
                                viewState.postValue(ServiceViewState.OnFailShopByCategory("Server Issue"))
                            }

                            403 -> {
                                viewState.postValue(ServiceViewState.OnFailShopByCategory("Denied"))
                            }

                            406 -> {
                                viewState.postValue(ServiceViewState.OnFailShopByCategory("Another Login"))
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    viewState.postValue(ServiceViewState.OnFailShopByCategory(e.message.toString()))
                }
            } else {
                viewState.postValue(ServiceViewState.OnListEndReachShop)
            }

        }
    }

    fun fetchShopWebLink(store_id: Int, customer_id: Int) {
        viewState.postValue(ServiceViewState.OnLoadingShopWebLink)
        viewModelScope.launch {
            try {
                val response = serviceRepository.fetchShopWebLink(store_id, customer_id)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(ServiceViewState.OnSuccessShopWebLink(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(ServiceViewState.OnFailShopWebLink("Server Issue"))
                        }

                        403 -> {
                            viewState.postValue(ServiceViewState.OnFailShopWebLink("Denied"))
                        }

                        406 -> {
                            viewState.postValue(ServiceViewState.OnFailShopWebLink("Another Login"))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewState.postValue(ServiceViewState.OnFailShopWebLink(e.message.toString()))

            }
        }
    }
}