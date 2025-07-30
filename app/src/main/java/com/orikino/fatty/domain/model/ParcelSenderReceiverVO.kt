package com.orikino.fatty.domain.model

data class ParcelSenderReceiverVO(
    val sender_name: String = "",
    val senderPhone: String = "",
    val sender_address: String = "",
    val sender_latitude: Double = 0.0,
    val sender_longitude: Double = 0.0,
    val sender_note: String = "",
    val receiver_name: String = "",
    val receiver_phone: String = "",
    val receiver_address: String = "",
    val to_latitude: Double = 0.0,
    val to_longitude: Double = 0.0,
    val receiver_note: String = "",
    val statePosition: Int = 0,
    val cityPosition: Int = 0,
    val state_id: Int = 0,
    val city_id : Int = 0,
    val fromParcelCityId: Int = 0,
    val toParcelCityId: Int = 0
)
