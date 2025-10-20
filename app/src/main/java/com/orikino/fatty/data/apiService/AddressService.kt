package com.orikino.fatty.data.apiService

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.utils.helper.ApiRouteConstant
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AddressService {

    @POST(ApiRouteConstant.routeCustomerAddressList)
    @FormUrlEncoded
    suspend fun fetchCustomerAddressList(@Field("customer_id") customer_id: Int): Response<CustomerAddressListResponse>


    @POST(ApiRouteConstant.routeCustomerAddressDelete)
    @FormUrlEncoded
    suspend fun deleteAddressById(@Field("customer_address_id") customer_address_id: Int): Response<DeleteAddressResponse>


    @POST(ApiRouteConstant.routeCustomerDefaultAddress)
    @FormUrlEncoded
    suspend fun setAsDefaultAddress(@Field("customer_address_id") customer_address_id: Int): Response<DefaultAddressResponse>

    @POST(ApiRouteConstant.routeCustomerAddress)
    @FormUrlEncoded
    suspend fun addCurrentAddress(
        @Field("customer_id") customer_id : Int,
        @Field("address_latitude") address_latitude : Double,
        @Field("address_longitude") address_longitude : Double,
        @Field("current_address") current_address : String,
        @Field("customer_phone") customer_phone : String,
        @Field("building_system") building_system : String,
        @Field("address_type") address_type : String,
        @Field("is_default") is_default : Boolean,
        @Field("secondary_phone") secondary_phone : String?
    ) : Response<CustomerAddressResponse>


    @POST(ApiRouteConstant.routeUpdateCurrentAddress)
    @FormUrlEncoded
    suspend fun updateCurrentAddress(
        @Field("customer_address_id") customer_address_id : Int,
        @Field("customer_id") customer_id : Int,
        @Field("address_latitude") address_latitude : Double,
        @Field("address_longitude") address_longitude : Double,
        @Field("current_address") current_address : String,
        @Field("customer_phone") customer_phone : String,
        @Field("building_system") building_system : String,
        @Field("address_type") address_type : String,
        @Field("is_default") is_default : Boolean,
        @Field("secondary_phone") secondary_phone : String?
    ) : Response<CustomerAddressResponse>

}