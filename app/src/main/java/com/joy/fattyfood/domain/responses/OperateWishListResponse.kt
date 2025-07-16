package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName
import com.joy.fattyfood.domain.model.CustomerWishListVO

data class OperateWishListResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("message")
    var message : String = "",
    @SerializedName("data")
    var data : CustomerWishListVO = CustomerWishListVO(),
    @SerializedName("code")
    var code : Int = 0
)
