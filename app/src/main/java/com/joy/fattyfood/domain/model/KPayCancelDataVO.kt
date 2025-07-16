package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class KPayCancelDataVO(
    @SerializedName("response")
    var response : KPayResponseVO ?= null
)
