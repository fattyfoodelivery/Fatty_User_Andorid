package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName

data class ShopWebLinkResponse(
    @SerializedName("success")
    var success : Boolean = false,

    @SerializedName("message")
    var message : String  = "",

    @SerializedName("data")
    var data : ShopWebLinkData? = null
)

data class ShopWebLinkData(
    @SerializedName("web_view_link")
    var web_view_link : String = "",

    @SerializedName("languages")
    var languages : List<String>? = null
)