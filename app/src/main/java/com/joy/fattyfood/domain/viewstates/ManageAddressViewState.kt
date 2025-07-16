package com.joy.fattyfood.domain.viewstates

import com.joy.fattyfood.domain.responses.*

sealed class ManageAddressViewState{
    object OnLoadingManageAddressList :  com.joy.fattyfood.domain.viewstates.ManageAddressViewState()
    data class OnSuccessManageAddressList(val data : CustomerAddressListResponse) : com.joy.fattyfood.domain.viewstates.ManageAddressViewState()
    data class OnFailManageAddressList(val message : String) : com.joy.fattyfood.domain.viewstates.ManageAddressViewState()

    data class OnSuccessDeleteAddress(val data : DeleteAddressResponse) : com.joy.fattyfood.domain.viewstates.ManageAddressViewState()
    data class OnFailDeleteAddress(val message: String) : com.joy.fattyfood.domain.viewstates.ManageAddressViewState()

    data class OnSuccessDefaultAddress(val data : DefaultAddressResponse) : com.joy.fattyfood.domain.viewstates.ManageAddressViewState()
    data class OnFailDefaultAddress(val message : String) : com.joy.fattyfood.domain.viewstates.ManageAddressViewState()
}

sealed class ManageParcelAddressViewState{
    object OnLoadingManageParcelAddressList :  com.joy.fattyfood.domain.viewstates.ManageParcelAddressViewState()
    data class OnSuccessManageParcelAddressList(val data : ParcelAddressListResponse) : com.joy.fattyfood.domain.viewstates.ManageParcelAddressViewState()
    data class OnFailManageParcelAddressList(val message : String) : com.joy.fattyfood.domain.viewstates.ManageParcelAddressViewState()

    data class OnSuccessDeleteParcelAddress(val data : DeleteParcelAddressResponse) : com.joy.fattyfood.domain.viewstates.ManageParcelAddressViewState()
    data class OnFailDeleteParcelAddress(val message: String) : com.joy.fattyfood.domain.viewstates.ManageParcelAddressViewState()

    data class OnSuccessAddParcelAddress(val data : AddParcelAddressResponse) : com.joy.fattyfood.domain.viewstates.ManageParcelAddressViewState()
    data class OnFailAddParcelAddress(val message: String) : com.joy.fattyfood.domain.viewstates.ManageParcelAddressViewState()

    data class OnSuccessUpdateParcelAddress(val data : UpdateParcelAddressResponse) : com.joy.fattyfood.domain.viewstates.ManageParcelAddressViewState()
    data class OnFailUpdateParcelAddress(val message: String) : com.joy.fattyfood.domain.viewstates.ManageParcelAddressViewState()

    data class OnSuccessDefaultParcelAddress(val data : DefaultParcelAddressResponse) : com.joy.fattyfood.domain.viewstates.ManageParcelAddressViewState()
    data class OnFailDefaultParcelAddress(val message : String) : com.joy.fattyfood.domain.viewstates.ManageParcelAddressViewState()
}
