package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*

sealed class TrackOrderViewState{

    object OnLoadingTrackFoodOrder :  TrackOrderViewState()
    data class OnSuccessTrackFoodOrder(val data : TrackFoodOrderResponse) : TrackOrderViewState()
    data class OnFailTrackFoodOrder(val message : String) : TrackOrderViewState()

    object OnLoadingRiderLocation :  TrackOrderViewState()
    data class OnSuccessRiderLocation(val data : RiderLocationResponse) : TrackOrderViewState()
    data class OnFailRiderLocation(val message : String) : TrackOrderViewState()


    object OnLoadingFoodDetailWithRating :  TrackOrderViewState()
    data class OnSuccessFoodDetailWithRating(val data : OrderDetailResponse) : TrackOrderViewState()
    data class OnFailFoodDetailWithRating(val message : String) : TrackOrderViewState()


}

sealed class TrackParcelViewState {

    object OnLoadingCheckParcel :  TrackParcelViewState()
    data class OnSuccessCheckParcel(val data : ParcelOrderInfoResponse) : TrackParcelViewState()
    data class OnFailCheckParcel(val message : String?) : TrackParcelViewState()

    object OnLoadingTrackParcelOrder :  TrackParcelViewState()
    data class OnSuccessTrackParcelOrder(val data : TrackFoodOrderResponse) : TrackParcelViewState()
    data class OnFailTrackParcelOrder(val message : String?) : TrackParcelViewState()

    object OnLoadingParcelDetailWithRating :  TrackParcelViewState()
    data class OnSuccessParcelDetailWithRating(val data : MyOrderHistoryResponse) : TrackParcelViewState()
    data class OnFailParcelDetailWithRating(val message : String?) : TrackParcelViewState()
}

sealed class OrderDetailWithRatingViewState {
    object OnLoadingOrderDetailDetailWithRating :  OrderDetailWithRatingViewState()
    data class OnSuccessOrderDetailWithRating(val data : OrderDetailWithRatingResponse) : OrderDetailWithRatingViewState()
    data class OnFailOrderDetailWithRating(val message : String?) : OrderDetailWithRatingViewState()
}