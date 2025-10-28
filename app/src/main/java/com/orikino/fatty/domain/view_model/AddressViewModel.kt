package com.orikino.fatty.domain.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orikino.fatty.data.repository.AddressRepository
import com.orikino.fatty.domain.model.CustomerAddressVO
import com.orikino.fatty.domain.viewstates.AddressViewState
import com.orikino.fatty.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val repository: AddressRepository
)  : ViewModel() {

    var viewState: MutableLiveData<com.orikino.fatty.domain.viewstates.AddressViewState> = MutableLiveData()
    var manageAddressLiveDataList: MutableLiveData<MutableList<CustomerAddressVO>> = MutableLiveData()

    fun customerAddressList(customerId: Int) {
        viewModelScope.launch (Dispatchers.IO){
            try {
                viewState.postValue(AddressViewState.OnLoadingCustomerAddressList)
                var response = repository.customerAddressList(customerId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(AddressViewState.OnSuccessCustomerAddressList(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(AddressViewState.OnFailCustomerAddressList(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(AddressViewState.OnFailCustomerAddressList(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(AddressViewState.OnFailCustomerAddressList(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(AddressViewState.OnFailCustomerAddressList(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun updateCurrentAddress(
        customer_address_id: Int,
        customer_id: Int,
        address_latitude: Double,
        address_longitude: Double,
        current_address: String,
        customer_phone: String,
        building_system: String,
        address_type: String,
        is_default : Boolean,
        otherPhone : String?
    ) {
        viewModelScope.launch {
            try {
                viewState.postValue(AddressViewState.OnLoadingAddCurrentAddress)
                var response = repository.updateCurrentAddress(
                    customer_address_id,
                    customer_id,
                    address_latitude,
                    address_longitude,
                    current_address,
                    customer_phone,
                    building_system,
                    address_type,
                    is_default,
                    otherPhone
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(AddressViewState.OnSuccessAddCurrentAddress(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(AddressViewState.OnFailAddCurrentAddress(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(AddressViewState.OnFailAddCurrentAddress(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(AddressViewState.OnFailAddCurrentAddress(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(AddressViewState.OnFailAddCurrentAddress(Constants.CONNECTION_ISSUE))
            }
        }

    }

    fun setUpDefaultAddress(customer_address_id: Int) {
        viewModelScope.launch {
            try {
                var response = repository.setAsDefaultAddress(customer_address_id)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(AddressViewState.OnSuccessSetDefaultAddress(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(AddressViewState.OnFailSetDefaultAddress(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(AddressViewState.OnFailSetDefaultAddress(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(AddressViewState.OnFailSetDefaultAddress(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(AddressViewState.OnFailSetDefaultAddress(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun deleteAddress(customer_address_id: Int) {
        viewModelScope.launch {
            try {
                var response = repository.deleteAddressById(customer_address_id)
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(AddressViewState.OnSuccessDeleteAddress(it))
                    } ?: run {
                        viewState.postValue(AddressViewState.OnFailDeleteAddress(Constants.FAILED))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(AddressViewState.OnFailDeleteAddress(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(AddressViewState.OnFailDeleteAddress(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(AddressViewState.OnFailDeleteAddress(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(AddressViewState.OnFailDeleteAddress(Constants.CONNECTION_ISSUE))
            }
        }
    }

    fun addCurrentAddress(
        customerId: Int,
        address_latitude: Double,
        address_longitude: Double,
        current_address: String,
        customer_phone: String,
        building_system: String,
        address_type: String,
        is_default : Boolean,
        otherPhone: String?
    ) {
        viewModelScope.launch {
            try {
                viewState.postValue(AddressViewState.OnLoadingAddCurrentAddress)
                var response = repository.addCurrentAddress(
                    customerId,
                    address_latitude,
                    address_longitude,
                    current_address,
                    customer_phone,
                    building_system,
                    address_type,
                    is_default,
                    otherPhone
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.postValue(AddressViewState.OnSuccessAddCurrentAddress(it))
                    }
                } else {
                    when (response.code()) {
                        500 -> {
                            viewState.postValue(AddressViewState.OnFailAddCurrentAddress(Constants.SERVER_ISSUE))
                        }

                        403 -> {
                            viewState.postValue(AddressViewState.OnFailAddCurrentAddress(Constants.DENIED))
                        }

                        406 -> {
                            viewState.postValue(AddressViewState.OnFailAddCurrentAddress(Constants.ANOTHER_LOGIN))
                        }
                    }
                }
            } catch (e: Exception) {
                viewState.postValue(AddressViewState.OnFailAddCurrentAddress(Constants.CONNECTION_ISSUE))
            }
        }

    }
}


