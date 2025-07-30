package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName


data class CurrencyVO(
    @SerializedName("currency_id")
    var currency_id: Int = 0,
    @SerializedName("currency_name")
    var currency_name: String = "",
    @SerializedName("currency_symbol")
    var currency_symbol : String ="",
    @SerializedName("image")
    var image: String = "",
    var position: Int = 0
)

