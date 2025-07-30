package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName
import com.orikino.fatty.domain.model.TotalEstimateCostVO

data class EstimateCostResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : TotalEstimateCostVO = TotalEstimateCostVO(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)
