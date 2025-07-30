package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*
sealed class AddressViewState{

    object OnLoadingCustomerAddressList :  com.orikino.fatty.domain.viewstates.AddressViewState()
    data class OnSuccessCustomerAddressList(val data : CustomerAddressListResponse) : com.orikino.fatty.domain.viewstates.AddressViewState()
    data class OnFailCustomerAddressList(val message: String) : com.orikino.fatty.domain.viewstates.AddressViewState()


    object OnLoadingAddCurrentAddress :  com.orikino.fatty.domain.viewstates.AddressViewState()
    data class OnSuccessAddCurrentAddress(val data : CustomerAddressResponse) : com.orikino.fatty.domain.viewstates.AddressViewState()
    data class OnFailAddCurrentAddress(val message: String) : com.orikino.fatty.domain.viewstates.AddressViewState()

    object OnLoadingUpdateCurrentAddress :  com.orikino.fatty.domain.viewstates.AddressViewState()
    data class OnSuccessUpdateCurrentAddress(val data : CustomerAddressResponse) : com.orikino.fatty.domain.viewstates.AddressViewState()
    data class OnFailUpdateCurrentAddress(val message: String) : com.orikino.fatty.domain.viewstates.AddressViewState()

    object OnLoadingSetDefaultAddress :  com.orikino.fatty.domain.viewstates.AddressViewState()
    data class OnSuccessSetDefaultAddress(val data : DefaultAddressResponse) : com.orikino.fatty.domain.viewstates.AddressViewState()
    data class OnFailSetDefaultAddress(val message: String) : com.orikino.fatty.domain.viewstates.AddressViewState()

    object OnLoadingDeleteAddress :  com.orikino.fatty.domain.viewstates.AddressViewState()
    data class OnSuccessDeleteAddress(val data : DeleteAddressResponse) : com.orikino.fatty.domain.viewstates.AddressViewState()
    data class OnFailDeleteAddress(val message: String) : com.orikino.fatty.domain.viewstates.AddressViewState()
}
