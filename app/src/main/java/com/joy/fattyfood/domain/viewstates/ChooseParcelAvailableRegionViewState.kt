package com.joy.fattyfood.domain.viewstates

import com.joy.fattyfood.domain.responses.*

sealed class ChooseParcelAvailableRegionViewState{
    object OnLoadingParcelAvailableRegion :  com.joy.fattyfood.domain.viewstates.ChooseParcelAvailableRegionViewState()
    data class OnSuccessParcelAvailableRegion(val data : ParcelAvailableRegionResponse) : com.joy.fattyfood.domain.viewstates.ChooseParcelAvailableRegionViewState()
    data class OnFailParcelAvailableRegion(val message : String) : com.joy.fattyfood.domain.viewstates.ChooseParcelAvailableRegionViewState()

    //object OnLoadingManageAddressList :  ChooseParcelAvailableRegionViewState()
    data class OnSuccessManageAddressList(val data : CustomerAddressListResponse) : com.joy.fattyfood.domain.viewstates.ChooseParcelAvailableRegionViewState()
    data class OnFailManageAddressList(val message : String) : com.joy.fattyfood.domain.viewstates.ChooseParcelAvailableRegionViewState()
}