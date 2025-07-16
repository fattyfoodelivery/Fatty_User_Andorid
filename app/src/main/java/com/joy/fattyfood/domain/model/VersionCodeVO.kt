package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class VersionCodeVO(
    @SerializedName("is_force_update")
    var is_force_update: Boolean = false,
    @SerializedName("is_update")
    var is_update: Boolean = false,
    @SerializedName("current_version")
    var current_version : String ?=null,
    @SerializedName("link")
    var link : String ?= null
)
