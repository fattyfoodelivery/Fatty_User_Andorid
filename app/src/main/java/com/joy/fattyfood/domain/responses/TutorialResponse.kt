package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName

data class TutorialResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : MutableList<TutorialVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class TutorialVO(
    @SerializedName("tutorial_id")
    var tutorial_id: Int = 0,
    @SerializedName("video")
    var video: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("photo")
    var photo: String = ""
)
