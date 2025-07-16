package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName
import com.joy.fattyfood.domain.model.CustomerVO

data class UpdateUserInfoResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("data")
    val data: CustomerVO = CustomerVO(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)







