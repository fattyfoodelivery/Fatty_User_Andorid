package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName
import com.orikino.fatty.domain.model.HelpCenterVO

data class AppHelpCenterResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : MutableList<HelpCenterVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)
