package com.orikino.fatty.domain.responses

import com.google.gson.annotations.SerializedName
import com.orikino.fatty.domain.model.InboxVO
import com.orikino.fatty.domain.model.NotiTypeVO
import com.orikino.fatty.domain.model.RatingType

data class InboxResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("data")
    var data: MutableList<InboxVO> = mutableListOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("code")
    var code: Int = 0,
    @SerializedName("current_page")
    var current_page: Int = 0,
    @SerializedName("last_page")
    var last_page: Int = 0
)

data class NotiTypeListResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("data")
    var data: MutableList<NotiTypeVO> = mutableListOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("code")
    var code: Int = 0
)

data class DeliverRatingResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("message")
    var message: String = "",
    @SerializedName("code")
    var code: Int = 0
)

data class RatingValueResponse(
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val data: NewRatingResponse? = null
)

data class NewRatingResponse(
    @SerializedName("rider")
    var rider: RatingType,
    @SerializedName("restaurant")
    var restaurant: RatingType
)

data class RatingResponse(
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("message")
    val message: String = ""
)