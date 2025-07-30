package com.orikino.fatty.domain.model

data class ManageAddress(
    var customer_address: CustomerAddressVO = CustomerAddressVO(),
    var status: Boolean = false,
    var isMapUpdate: Boolean = false,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
)
