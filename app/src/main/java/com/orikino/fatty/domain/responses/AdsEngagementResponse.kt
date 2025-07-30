package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName

data class AdsEngagementResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("message")
    var message : String = ""
)
