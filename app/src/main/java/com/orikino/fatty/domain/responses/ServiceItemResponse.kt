package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName

data class ServiceItemResponse(
    @SerializedName("success")
    var success : Boolean = false,

    @SerializedName("message")
    var message : String  = "",

    @SerializedName("data")
    var data : List<ServiceItem> = emptyList()
)

data class ServiceItem(
    @SerializedName("service_item_id")
    var service_item_id : Int = 0,

    @SerializedName("name")
    var name : String = "",

    @SerializedName("sub_title")
    var sub_title : String = "",

    @SerializedName("image")
    var image : String = "",

    @SerializedName("cover_image")
    var cover_image : String? = null
)