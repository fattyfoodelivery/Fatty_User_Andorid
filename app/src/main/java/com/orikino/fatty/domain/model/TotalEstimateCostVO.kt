package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class TotalEstimateCostVO(
    @SerializedName("define_cost")
    var define_cost: MutableList<DefineDeliveryCostVO>? = null,
    @SerializedName("estimated_cost")
    var estimated_cost: EstimateCostVO? = null
)
