package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName
import com.joy.fattyfood.domain.model.CurrencyVO

data class CurrencyResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : MutableList<CurrencyVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = ""
)