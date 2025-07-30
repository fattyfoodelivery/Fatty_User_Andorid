package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class EstimateCostVO(
    @SerializedName("delivery_fee")
    var delivery_fee: Double = 0.0,
    @SerializedName("extra_coverage")
    var extra_coverage: Double = 0.0,
    @SerializedName("weight_fee")
    var weight_fee: Double = 0.0,
    @SerializedName("total_estimated")
    var total_estimated: Double = 0.0,
    @SerializedName("delivery_fee_currency")
    var delivery_fee_currency: Double = 0.0,
    @SerializedName("extra_coverage_currency")
    var extra_coverage_currency: Double = 0.0,
    @SerializedName("total_estimated_currency")
    var total_estimated_currency: Double = 0.0,
    @SerializedName("exchange_rate")
    var exchange_rate: Double = 0.0,
    @SerializedName("currency_id")
    var currency_id : Int = 0,
    @SerializedName("abnormal_fee")
    var abnormal_fee: Double = 0.0
)
