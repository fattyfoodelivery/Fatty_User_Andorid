package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*

sealed class ChooseParcelAvailableRegionViewState{
    object OnLoadingParcelAvailableRegion :  com.orikino.fatty.domain.viewstates.ChooseParcelAvailableRegionViewState()
    data class OnSuccessParcelAvailableRegion(val data : ParcelAvailableRegionResponse) : com.orikino.fatty.domain.viewstates.ChooseParcelAvailableRegionViewState()
    data class OnFailParcelAvailableRegion(val message : String) : com.orikino.fatty.domain.viewstates.ChooseParcelAvailableRegionViewState()

    //object OnLoadingManageAddressList :  ChooseParcelAvailableRegionViewState()
    data class OnSuccessManageAddressList(val data : CustomerAddressListResponse) : com.orikino.fatty.domain.viewstates.ChooseParcelAvailableRegionViewState()
    data class OnFailManageAddressList(val message : String) : com.orikino.fatty.domain.viewstates.ChooseParcelAvailableRegionViewState()
}