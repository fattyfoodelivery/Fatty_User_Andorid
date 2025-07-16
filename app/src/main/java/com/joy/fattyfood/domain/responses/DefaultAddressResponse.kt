package com.joy.fattyfood.domain.responses

import com.google.gson.annotations.SerializedName
import com.joy.fattyfood.domain.model.CustomerAddressVO
import com.joy.fattyfood.domain.model.ParcelAvailableRegionVO
import java.io.Serializable

data class DefaultAddressResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("data")
    var data: MutableList<CustomerAddressVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)




data class CustomerAddressResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("data")
    var data: CustomerAddressVO = CustomerAddressVO(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class DeleteAddressResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("data")
    var data: MutableList<CustomerAddressVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class ParcelAddressListResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("data")
    var data: MutableList<ParcelAddressVO> = mutableListOf(),
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class ParcelAddressVO(
    @SerializedName("parcel_default_address_id")
    var parcel_default_address_id: Int? = 0,
    @SerializedName("customer_id")
    var customer_id: Int? = 0,
    @SerializedName("phone")
    var phone: String? = null,
    @SerializedName("address")
    var address: String? = null,
    @SerializedName("is_default")
    var is_default: Boolean? = false,
    @SerializedName("parcel_block")
    var parcel_block: ParcelAvailableRegionVO? = null,
    var isSelected: Boolean = false
) : Serializable

data class DefaultParcelAddressResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class UpdateParcelAddressResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class AddParcelAddressResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)

data class DeleteParcelAddressResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("message")
    var message : String  = "",
    @SerializedName("code")
    var code : Int = 0
)