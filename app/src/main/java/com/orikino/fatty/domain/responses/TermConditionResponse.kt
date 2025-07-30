package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName

data class TermConditionResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : TermAndConditionVO = TermAndConditionVO(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class TermAndConditionVO(
    @SerializedName("terms_conditions_id")
    var terms_conditions_id: Int = 0,
    @SerializedName("terms_conditions_type")
    var terms_conditions_type: String = "",
    @SerializedName("body")
    var body: String = ""
)
