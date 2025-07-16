package com.joy.fattyfood.domain.viewstates

import com.joy.fattyfood.domain.responses.*

sealed class CreateFoodOrderViewState{
    data class OnSuccessPaymentMethod(val data : PaymentMethodResponse) : com.joy.fattyfood.domain.viewstates.CreateFoodOrderViewState()
    data class OnFailPaymentMethod(val message : String) : com.joy.fattyfood.domain.viewstates.CreateFoodOrderViewState()

    object OnLoadingCreateFoodOrder :  com.joy.fattyfood.domain.viewstates.CreateFoodOrderViewState()
    data class OnSuccessCreateFoodOrder(val data : OrderResponse) : com.joy.fattyfood.domain.viewstates.CreateFoodOrderViewState()
    data class OnFailCreateFoodOrder(val message : String) : com.joy.fattyfood.domain.viewstates.CreateFoodOrderViewState()

   // object OnLoadingFoodDeliveryFee :  CreateFoodOrderViewState()
    data class OnSuccessFoodDeliveryFee(val data : FoodDeliveryFeeResponse) : com.joy.fattyfood.domain.viewstates.CreateFoodOrderViewState()
    data class OnFailFoodDeliveryFee(val message: String) : com.joy.fattyfood.domain.viewstates.CreateFoodOrderViewState()

    object OnLoadingRefreshFoodDeliveryFee :  com.joy.fattyfood.domain.viewstates.CreateFoodOrderViewState()
    data class OnSuccessRefreshFoodDeliveryFee(val data : FoodDeliveryFeeResponse) : com.joy.fattyfood.domain.viewstates.CreateFoodOrderViewState()
    data class OnFailRefreshFoodDeliveryFee(val message: String) : com.joy.fattyfood.domain.viewstates.CreateFoodOrderViewState()

    data class OnSuccessManageAddressList(val data : CustomerAddressListResponse) : com.joy.fattyfood.domain.viewstates.CreateFoodOrderViewState()
    data class OnFailManageAddressList(val message : String) : com.joy.fattyfood.domain.viewstates.CreateFoodOrderViewState()
}

