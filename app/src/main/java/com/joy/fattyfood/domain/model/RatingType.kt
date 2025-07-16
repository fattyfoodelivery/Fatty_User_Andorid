package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class RatingType(
    @SerializedName("good")
    var good: MutableList<RatingValueVO> = mutableListOf(),
    @SerializedName("bad")
    var bad: MutableList<RatingValueVO> = mutableListOf()
)
