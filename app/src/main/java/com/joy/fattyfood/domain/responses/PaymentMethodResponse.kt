package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName
import com.joy.fattyfood.domain.model.PaymentMethodVO

data class PaymentMethodResponse(
    @SerializedName("success")
    var success : Boolean = true,
    @SerializedName("data")
    var data : MutableList<PaymentMethodVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)


