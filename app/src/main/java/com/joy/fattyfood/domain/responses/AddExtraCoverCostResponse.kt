package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName
import com.joy.fattyfood.domain.model.AddExtraCoverCostVO

data class AddExtraCoverCostResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : MutableList<AddExtraCoverCostVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)
