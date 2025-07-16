package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName
import com.joy.fattyfood.domain.model.ActiveOrderVO

data class OrderHistoriesResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("message")
    var message : String ="",
    @SerializedName("active_order")
    var active_order : MutableList<ActiveOrderVO> = mutableListOf(),
    @SerializedName("past_order")
    var past_order : MutableList<ActiveOrderVO> = mutableListOf(),
    @SerializedName("code")
    var code : Int = 0,
    @SerializedName("current_page")
    var current_page : Int = 0,
    @SerializedName("last_page")
    var last_page : Int = 0
)
