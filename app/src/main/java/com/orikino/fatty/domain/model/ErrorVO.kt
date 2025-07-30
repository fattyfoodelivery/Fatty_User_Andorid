package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class ErrorVO(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)
