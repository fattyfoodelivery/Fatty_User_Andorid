package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName

data class LogoutResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)
