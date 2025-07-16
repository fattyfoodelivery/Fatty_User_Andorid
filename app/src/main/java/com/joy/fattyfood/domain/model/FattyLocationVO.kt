package com.joy.fattyfood.domain.model

data class FattyLocationVO(
    val lat : Double ?= 0.0,
    val lng : Double ?= 0.0
)

data class FattyMessageVO(
    val messageText: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val orderId: String = "",
    val channel: String = "",
    val sentAt: Long = System.currentTimeMillis()

)