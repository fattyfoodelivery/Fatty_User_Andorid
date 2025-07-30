package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class PrivacyPolicyVO(
    @SerializedName("privacy_id")
    var privacy_id: Int = 0,
    @SerializedName("privacy_type")
    var privacy_type: String = "",
    @SerializedName("body")
    var body: String = ""
)
