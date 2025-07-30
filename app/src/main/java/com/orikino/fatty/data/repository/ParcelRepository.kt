package com.orikino.fatty.data.repository

import com.orikino.fatty.domain.responses.*
import retrofit2.Response

interface ParcelRepository {

    suspend fun parcelOrderById(
        customer_id : Int,
        parcel_zone_id : Int
    ) : Response<ParcelOrderInfoResponse>

    suspend fun fetchCurrency() : Response<CurrencyResponse>



    /*suspend fun fetchParcelAddressList(customerId : Int,zoneId : Int) : Response<ParcelAddressListResponse>
    suspend fun addParcelAddress(customerId: Int,parcelBlockId : Int,phone : String,address : String) : Response<AddParcelAddressResponse>
    suspend fun updateParcelAddress(parcelDefaultAddress_id : Int,customerId: Int,parcelBlockId : Int,phone : String,address : String) : Response<UpdateParcelAddressResponse>
    suspend fun deleteParcelAddressById(parcel_default_address_id : Int) : Response<DeleteParcelAddressResponse>
    suspend fun setAsDefaultParcelAddress(parcel_default_address_id : Int) : Response<DefaultParcelAddressResponse>
    suspend fun bookingParcel(bookingParcelVO: BookingParcelVO) : Response<ParcelBookingResponse>

    suspend fun fetchEstimateCost(
        from_pickup_latitude : Double,
        from_pickup_longitude : Double,
        to_drop_latitude : Double,
        to_drop_longitude : Double,
        parcel_extra_cover_id : Int,
        total_estimated_weight : Double
    ) : Response<EstimateCostResponse>

    suspend fun fetchParcelAvailableRegion(customerId : Int): Response<ParcelAvailableRegionResponse>
    suspend fun fetchCustomerAddressList(customer_id : Int) : Response<CustomerAddressListResponse>*/
}