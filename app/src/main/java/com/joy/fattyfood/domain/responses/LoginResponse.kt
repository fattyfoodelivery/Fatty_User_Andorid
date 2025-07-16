package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName
import com.joy.fattyfood.domain.model.CustomerVO


data class LoginResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("data")
    val data : ResponseVO = ResponseVO(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class ResponseVO(
    @SerializedName("customer")
    val customer : CustomerVO = CustomerVO(),
    @SerializedName("is_old")
    var is_old : Boolean = false,
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0,
    @SerializedName("type")
    var type : Int = 0

)

data class RequestPhoneOtpResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("data")
    var data : DataTypeV0 = DataTypeV0()
)

data class DataTypeV0(
    @SerializedName("type")
    var type : Int = 0
)

/*
data class RequestPhoneOtpResponse(
    @SerializedName("success")
    var success : Boolean = false,
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("data")
    var data : String? = null
)*/
