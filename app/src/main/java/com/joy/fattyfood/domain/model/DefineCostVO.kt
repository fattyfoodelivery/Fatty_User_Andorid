package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class DefineCostVO(
    @SerializedName("weight")
    var weight : String ?=null,
    @SerializedName("delivery_fee")
    var delivery_fee : Int ?= null
)
