package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*

sealed class OrderViewState{

    object OnLoadingMyOrder :  OrderViewState()
    data class OnSuccessMyOrder(val data : MyOrderHistoryResponse) :OrderViewState()
    data class OnFailMyOrder(val message: String?) : OrderViewState()

    data class OnSuccessOrderCancel(val data : FoodOrderCancelResponse) : OrderViewState()
    data class OnFailOrderCancel(val message: String) : OrderViewState()


    data class OnSuccessPaymentMethod(val data : PaymentMethodResponse) : OrderViewState()
    data class OnFailPaymentMethod(val message: String?) : OrderViewState()

    object OnLoadingCreateFoodOrder :  OrderViewState()
    data class OnSuccessCreateFoodOrder(val data : OrderResponse) : OrderViewState()
    data class OnFailCreateFoodOrder(val message: String?) : OrderViewState()

    // object OnLoadingFoodDeliveryFee :  CreateFoodOrderViewState()
    data class OnSuccessFoodDeliveryFee(val data : FoodDeliveryFeeResponse) : OrderViewState()
    data class OnFailFoodDeliveryFee(val message: String?) : OrderViewState()

    object OnLoadingRefreshFoodDeliveryFee :  OrderViewState()
    data class OnSuccessRefreshFoodDeliveryFee(val data : FoodDeliveryFeeResponse) : OrderViewState()
    data class OnFailRefreshFoodDeliveryFee(val message: String?) : OrderViewState()

    data class OnSuccessManageAddressList(val data : CustomerAddressListResponse) : OrderViewState()
    data class OnFailManageAddressList(val message: String?) : OrderViewState()

    object OnLoadingTrackFoodOrder : OrderViewState()
    data class OnSuccessTrackFoodOrder(val data : TrackFoodOrderResponse) :OrderViewState()
    data class OnFailTrackFoodOrder(val message : String?) : OrderViewState()

    object OnLoadingOrderDetailForReview : OrderViewState()
    data class OnSuccessOrderDetailForReview(val data : OrderDetailResponse) :OrderViewState()
    data class OnFailOrderDetailForReview(val message : String?) : OrderViewState()
}
