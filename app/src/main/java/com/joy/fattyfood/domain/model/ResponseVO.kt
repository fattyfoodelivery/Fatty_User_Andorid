package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class ResponseVO(
    @SerializedName("customer")
    val customer : CustomerVO = CustomerVO(),
    @SerializedName("is_old")
    var is_old : Boolean = false,
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0,
    @SerializedName("type")
    var type : Int = 0
)
