package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*

sealed class TrackFoodOrderViewState{
    object OnLoadingTrackFoodOrder :  TrackFoodOrderViewState()
    data class OnSuccessTrackFoodOrder(val data : TrackFoodOrderResponse) : TrackFoodOrderViewState()
    data class OnFailTrackFoodOrder(val message : String) : TrackFoodOrderViewState()

    data class OnSuccessFetchRiderLocation(val data : RiderLocationResponse) : TrackFoodOrderViewState()
    data class OnFaiFetchRiderLocationTrackFoodOrder(val message : String) : TrackFoodOrderViewState()

    object OnLoadingTrackParcelOrder :  TrackFoodOrderViewState()
    data class OnSuccessTrackParcelOrder(val data : TrackFoodOrderResponse) : TrackFoodOrderViewState()
    data class OnFailTrackParcelOrder(val message : String) : TrackFoodOrderViewState()
}


sealed class TrackParcelOrderViewState{
    object OnLoadingTrackParcelOrder :  TrackParcelOrderViewState()
    data class OnSuccessTrackParcelOrder(val data : TrackFoodOrderResponse) : TrackParcelOrderViewState()
    data class OnFailTrackParcelOrder(val message : String) : TrackParcelOrderViewState()

    data class OnSuccessFetchRiderLocation(val data : RiderLocationResponse) : TrackParcelOrderViewState()
    data class OnFaiFetchRiderLocation(val message : String) : TrackParcelOrderViewState()
}

