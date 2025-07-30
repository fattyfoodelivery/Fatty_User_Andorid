package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName
import com.orikino.fatty.domain.model.KPayCancelDataVO

data class FoodOrderCancelResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("message")
    var message : String = "",
    @SerializedName("data")
    var data : KPayCancelDataVO = KPayCancelDataVO(),
    @SerializedName("code")
    var code : Int = 0
)