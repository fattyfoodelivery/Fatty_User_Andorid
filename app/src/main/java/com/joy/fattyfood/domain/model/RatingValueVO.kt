package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class RatingValueVO(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("option")
    var option: String = "",
    @SerializedName("rating_type")
    var rating_type: String = ""
)
