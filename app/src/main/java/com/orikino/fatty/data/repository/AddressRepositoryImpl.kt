package com.orikino.fatty.data.repository

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.data.apiService.AddressService
import retrofit2.Response
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val addressService: AddressService
) : AddressRepository {

    override suspend fun customerAddressList(customer_address_id: Int) : Response<CustomerAddressListResponse> = addressService.fetchCustomerAddressList(customer_address_id)
    override suspend fun deleteAddressById(customer_address_id: Int): Response<DeleteAddressResponse> = addressService.deleteAddressById(
        customer_address_id
    )

    override suspend fun setAsDefaultAddress(customer_address_id: Int): Response<DefaultAddressResponse> = addressService.setAsDefaultAddress(
        customer_address_id
    )

    override suspend fun addCurrentAddress(
        customer_id: Int,
        address_latitude: Double,
        address_longitude: Double,
        current_address: String,
        customer_phone: String,
        building_system: String,
        address_type: String,
        is_default : Boolean,
        secondary_phone : String?
    ): Response<CustomerAddressResponse>  = addressService.addCurrentAddress(customer_id, address_latitude, address_longitude, current_address, customer_phone, building_system, address_type, is_default, secondary_phone)

    override suspend fun updateCurrentAddress(
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
    ): Response<CustomerAddressResponse> = addressService.updateCurrentAddress(customer_address_id, customer_id, address_latitude, address_longitude, current_address, customer_phone, building_system, address_type, is_default, secondary_phone)

    override suspend fun deleteCustomerAddressById(customer_address_id: Int): Response<DeleteAddressResponse> = addressService.deleteAddressById(customer_address_id)
}
