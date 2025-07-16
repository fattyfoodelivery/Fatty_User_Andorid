package com.joy.fattyfood.domain.viewstates

import com.joy.fattyfood.domain.responses.*
sealed class AddressViewState{

    object OnLoadingCustomerAddressList :  com.joy.fattyfood.domain.viewstates.AddressViewState()
    data class OnSuccessCustomerAddressList(val data : CustomerAddressListResponse) : com.joy.fattyfood.domain.viewstates.AddressViewState()
    data class OnFailCustomerAddressList(val message: String) : com.joy.fattyfood.domain.viewstates.AddressViewState()


    object OnLoadingAddCurrentAddress :  com.joy.fattyfood.domain.viewstates.AddressViewState()
    data class OnSuccessAddCurrentAddress(val data : CustomerAddressResponse) : com.joy.fattyfood.domain.viewstates.AddressViewState()
    data class OnFailAddCurrentAddress(val message: String) : com.joy.fattyfood.domain.viewstates.AddressViewState()

    object OnLoadingUpdateCurrentAddress :  com.joy.fattyfood.domain.viewstates.AddressViewState()
    data class OnSuccessUpdateCurrentAddress(val data : CustomerAddressResponse) : com.joy.fattyfood.domain.viewstates.AddressViewState()
    data class OnFailUpdateCurrentAddress(val message: String) : com.joy.fattyfood.domain.viewstates.AddressViewState()

    object OnLoadingSetDefaultAddress :  com.joy.fattyfood.domain.viewstates.AddressViewState()
    data class OnSuccessSetDefaultAddress(val data : DefaultAddressResponse) : com.joy.fattyfood.domain.viewstates.AddressViewState()
    data class OnFailSetDefaultAddress(val message: String) : com.joy.fattyfood.domain.viewstates.AddressViewState()

    object OnLoadingDeleteAddress :  com.joy.fattyfood.domain.viewstates.AddressViewState()
    data class OnSuccessDeleteAddress(val data : DeleteAddressResponse) : com.joy.fattyfood.domain.viewstates.AddressViewState()
    data class OnFailDeleteAddress(val message: String) : com.joy.fattyfood.domain.viewstates.AddressViewState()
}
