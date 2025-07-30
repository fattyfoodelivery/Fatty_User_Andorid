package com.orikino.fatty.domain.model

data class FattyMessage(
    val messageText: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val orderId: String = "",
    val channel: String = "",
    val sentAt: Long = System.currentTimeMillis()

)