package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName
import com.orikino.fatty.domain.model.PrivacyPolicyVO

data class PrivacyPolicyResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    var data : PrivacyPolicyVO = PrivacyPolicyVO(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)
