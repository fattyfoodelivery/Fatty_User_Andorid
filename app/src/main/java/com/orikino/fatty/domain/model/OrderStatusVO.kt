package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class OrderStatusVO(
    @SerializedName("order_status_id")
    var order_status_id : Int = 0 ,
    @SerializedName("order_status_name")
    var order_status_name : String = "",
    @SerializedName("order_type")
    var order_type : String = ""
)
