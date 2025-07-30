package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName

data class VersionUpdateResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("message")
    var message : String = ""
)
