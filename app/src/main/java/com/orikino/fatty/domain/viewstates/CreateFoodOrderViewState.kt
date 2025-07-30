package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*

sealed class CreateFoodOrderViewState{
    data class OnSuccessPaymentMethod(val data : PaymentMethodResponse) : com.orikino.fatty.domain.viewstates.CreateFoodOrderViewState()
    data class OnFailPaymentMethod(val message : String) : com.orikino.fatty.domain.viewstates.CreateFoodOrderViewState()

    object OnLoadingCreateFoodOrder :  com.orikino.fatty.domain.viewstates.CreateFoodOrderViewState()
    data class OnSuccessCreateFoodOrder(val data : OrderResponse) : com.orikino.fatty.domain.viewstates.CreateFoodOrderViewState()
    data class OnFailCreateFoodOrder(val message : String) : com.orikino.fatty.domain.viewstates.CreateFoodOrderViewState()

   // object OnLoadingFoodDeliveryFee :  CreateFoodOrderViewState()
    data class OnSuccessFoodDeliveryFee(val data : FoodDeliveryFeeResponse) : com.orikino.fatty.domain.viewstates.CreateFoodOrderViewState()
    data class OnFailFoodDeliveryFee(val message: String) : com.orikino.fatty.domain.viewstates.CreateFoodOrderViewState()

    object OnLoadingRefreshFoodDeliveryFee :  com.orikino.fatty.domain.viewstates.CreateFoodOrderViewState()
    data class OnSuccessRefreshFoodDeliveryFee(val data : FoodDeliveryFeeResponse) : com.orikino.fatty.domain.viewstates.CreateFoodOrderViewState()
    data class OnFailRefreshFoodDeliveryFee(val message: String) : com.orikino.fatty.domain.viewstates.CreateFoodOrderViewState()

    data class OnSuccessManageAddressList(val data : CustomerAddressListResponse) : com.orikino.fatty.domain.viewstates.CreateFoodOrderViewState()
    data class OnFailManageAddressList(val message : String) : com.orikino.fatty.domain.viewstates.CreateFoodOrderViewState()
}

