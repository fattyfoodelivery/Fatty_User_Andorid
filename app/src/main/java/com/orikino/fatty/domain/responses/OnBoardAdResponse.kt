package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName
import com.orikino.fatty.domain.model.OnBoardAdsVO

data class OnBoardAdResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : OnBoardAdsVO = OnBoardAdsVO(),
    @SerializedName("message")
    var message : String  = ""
)
