package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*

sealed class ParcelViewState{

    object OnLoadingParcelOrderDetail : ParcelViewState()
    data class OnSuccessParcelOrderDetail (val data : ParcelOrderInfoResponse) : ParcelViewState()
    data class OnFailParcelParcelOrderDetail(val message : String) : ParcelViewState()

    object OnLoadingCurrency : ParcelViewState()
    data class OnSuccessCurrency(val data : CurrencyResponse) : ParcelViewState()
    data class OnFailCurrency(val message : String) : ParcelViewState()
}

    /*

    object OnLoadingParcelBooking : ParcelViewState()
    data class OnSuccessParcelBooking(val data: ParcelBookingResponse) : ParcelViewState()
    data class OnFailParcelBooking(val message: String) : ParcelViewState()

    data class OnSuccessDeliveryFee(val data : EstimateCostResponse) : ParcelViewState()
    data class OnFailDeliveryFee(val message: String) :ParcelViewState()
}*/

sealed class ParcelBookingViewState {
    object OnLoadingParcelBooking : com.orikino.fatty.domain.viewstates.ParcelBookingViewState()
    data class OnSuccessParcelBooking(val data: ParcelBookingResponse) : com.orikino.fatty.domain.viewstates.ParcelBookingViewState()
    data class OnFailParcelBooking(val message: String) : com.orikino.fatty.domain.viewstates.ParcelBookingViewState()

    data class OnSuccessEstimateCost(val data : EstimateCostResponse) : com.orikino.fatty.domain.viewstates.ParcelBookingViewState()
    data class OnFailEstimateCost(val message : String) : com.orikino.fatty.domain.viewstates.ParcelBookingViewState()
}

sealed class ParcelStateCityViewState{
    object OnLoadingStateCity : com.orikino.fatty.domain.viewstates.ParcelStateCityViewState()
    data class OnSuccessStateCity(val data : StateCityResponse) : com.orikino.fatty.domain.viewstates.ParcelStateCityViewState()
    data class OnFailStateCity(val message : String) : com.orikino.fatty.domain.viewstates.ParcelStateCityViewState()
}