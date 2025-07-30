package com.orikino.fatty.domain.model

import com.google.gson.annotations.SerializedName

data class KPayResponseVO(
    @SerializedName("result")
    var result : String ="",
    @SerializedName("code")
    var code : String = "",
    @SerializedName("merch_order_id")
    var merch_order_id : String = "",
    @SerializedName("prepay_id")
    var prepay_id : String = "",
    @SerializedName("nonce_str")
    var nonce_str : String = "",
    @SerializedName("sign_type")
    var sign_type : String = "",
    @SerializedName("sign")
    var sign : String = "",
    @SerializedName("refund_amount")
    var refund_amount : String = ""
)
