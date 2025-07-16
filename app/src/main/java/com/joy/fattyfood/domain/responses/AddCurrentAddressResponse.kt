package com.fattyfood.delivery.network.responses

import com.google.gson.annotations.SerializedName

data class AddCurrentAddressResponse(
    @SerializedName("success")
    var success : Boolean = false
)
