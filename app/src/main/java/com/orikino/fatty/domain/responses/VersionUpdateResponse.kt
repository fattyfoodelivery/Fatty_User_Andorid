package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName

data class VersionUpdateResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("message")
    var message : String = "",
    @SerializedName("data")
    var data : VersionCodeData? = null
)

data class VersionCodeData(
    @SerializedName("current_version")
    var current_version : String = "",
    @SerializedName("is_update")
    var is_update : Boolean = false,
    @SerializedName("is_force_update")
    var is_force_update : Boolean = false,
    @SerializedName("link")
    var link : String = ""
)

