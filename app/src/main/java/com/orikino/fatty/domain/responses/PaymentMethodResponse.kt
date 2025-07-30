package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName
import com.orikino.fatty.domain.model.PaymentMethodVO

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


