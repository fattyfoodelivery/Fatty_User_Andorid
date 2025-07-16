package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName
import com.joy.fattyfood.domain.model.ActiveOrderVO
import com.joy.fattyfood.domain.model.RiderVO

data class TrackFoodOrderResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : ActiveOrderVO = ActiveOrderVO(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class RiderLocationResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : RiderVO = RiderVO(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)
