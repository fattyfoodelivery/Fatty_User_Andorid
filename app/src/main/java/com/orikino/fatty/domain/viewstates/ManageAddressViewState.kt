package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*

sealed class ManageAddressViewState{
    object OnLoadingManageAddressList :  com.orikino.fatty.domain.viewstates.ManageAddressViewState()
    data class OnSuccessManageAddressList(val data : CustomerAddressListResponse) : com.orikino.fatty.domain.viewstates.ManageAddressViewState()
    data class OnFailManageAddressList(val message : String) : com.orikino.fatty.domain.viewstates.ManageAddressViewState()

    data class OnSuccessDeleteAddress(val data : DeleteAddressResponse) : com.orikino.fatty.domain.viewstates.ManageAddressViewState()
    data class OnFailDeleteAddress(val message: String) : com.orikino.fatty.domain.viewstates.ManageAddressViewState()

    data class OnSuccessDefaultAddress(val data : DefaultAddressResponse) : com.orikino.fatty.domain.viewstates.ManageAddressViewState()
    data class OnFailDefaultAddress(val message : String) : com.orikino.fatty.domain.viewstates.ManageAddressViewState()
}

sealed class ManageParcelAddressViewState{
    object OnLoadingManageParcelAddressList :  com.orikino.fatty.domain.viewstates.ManageParcelAddressViewState()
    data class OnSuccessManageParcelAddressList(val data : ParcelAddressListResponse) : com.orikino.fatty.domain.viewstates.ManageParcelAddressViewState()
    data class OnFailManageParcelAddressList(val message : String) : com.orikino.fatty.domain.viewstates.ManageParcelAddressViewState()

    data class OnSuccessDeleteParcelAddress(val data : DeleteParcelAddressResponse) : com.orikino.fatty.domain.viewstates.ManageParcelAddressViewState()
    data class OnFailDeleteParcelAddress(val message: String) : com.orikino.fatty.domain.viewstates.ManageParcelAddressViewState()

    data class OnSuccessAddParcelAddress(val data : AddParcelAddressResponse) : com.orikino.fatty.domain.viewstates.ManageParcelAddressViewState()
    data class OnFailAddParcelAddress(val message: String) : com.orikino.fatty.domain.viewstates.ManageParcelAddressViewState()

    data class OnSuccessUpdateParcelAddress(val data : UpdateParcelAddressResponse) : com.orikino.fatty.domain.viewstates.ManageParcelAddressViewState()
    data class OnFailUpdateParcelAddress(val message: String) : com.orikino.fatty.domain.viewstates.ManageParcelAddressViewState()

    data class OnSuccessDefaultParcelAddress(val data : DefaultParcelAddressResponse) : com.orikino.fatty.domain.viewstates.ManageParcelAddressViewState()
    data class OnFailDefaultParcelAddress(val message : String) : com.orikino.fatty.domain.viewstates.ManageParcelAddressViewState()
}
