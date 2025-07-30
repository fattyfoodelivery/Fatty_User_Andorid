package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName

data class AboutAppResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : AboutVO = AboutVO(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class AboutVO(
    @SerializedName("about_id")
    var about_id : Int = 0,
    @SerializedName("about_type")
    var about_type : String = "",
    @SerializedName("about")
    var about : String  = "",
)
