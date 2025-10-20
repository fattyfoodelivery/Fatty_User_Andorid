package com.orikino.fatty.data.repository


import com.orikino.fatty.domain.responses.*
import retrofit2.Response

interface AddressRepository {

    suspend fun customerAddressList(customer_address_id: Int) : Response<CustomerAddressListResponse>

    suspend fun deleteAddressById(customer_address_id: Int): Response<DeleteAddressResponse>

    suspend fun setAsDefaultAddress(customer_address_id: Int): Response<DefaultAddressResponse>

    suspend fun addCurrentAddress(
        customer_id: Int,
        address_latitude: Double,
        address_longitude: Double,
        current_address: String,
        customer_phone: String,
        building_system: String,
        address_type: String,
        is_default : Boolean,
        secondary_phone : String?) : Response<CustomerAddressResponse>

    suspend fun updateCurrentAddress(
        customer_address_id: Int,
        customer_id: Int,
        address_latitude: Double,
        address_longitude: Double,
        current_address: String,
        customer_phone: String,
        building_system: String,
        address_type: String,
        is_default : Boolean,
        secondary_phone : String?
    ) : Response<CustomerAddressResponse>


    suspend fun deleteCustomerAddressById(customer_address_id : Int) : Response<DeleteAddressResponse>


}
