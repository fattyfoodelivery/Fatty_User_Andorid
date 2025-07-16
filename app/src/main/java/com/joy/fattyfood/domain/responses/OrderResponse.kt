package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName
import com.joy.fattyfood.domain.model.ActiveOrderVO
import com.joy.fattyfood.domain.model.CreateNewFoodOrderVO
import com.joy.fattyfood.domain.model.FoodDeliveryFeeVO
import com.joy.fattyfood.domain.model.KPayResponseVO

data class OrderResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("message")
    var message: String = "",
    @SerializedName("data")
    var data: DataVO? = null,
    @SerializedName("code")
    var code: Int = 0
) {
    data class DataVO(
        @SerializedName("response")
        var response : KPayResponseVO?= null,
        @SerializedName("order")
        var order : CreateNewFoodOrderVO?= CreateNewFoodOrderVO()
    )
}


data class ParcelBookingResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("message")
    var message: String = "",
    @SerializedName("data")
    var data: ActiveOrderVO = ActiveOrderVO(),
    @SerializedName("code")
    var code: Int = 0
)

data class FoodDeliveryFeeResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("data")
    var data: FoodDeliveryFeeVO = FoodDeliveryFeeVO(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("code")
    var code: Int = 0
)