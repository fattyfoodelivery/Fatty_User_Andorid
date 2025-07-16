package com.joy.fattyfood.data.apiService

import com.joy.fattyfood.domain.responses.*
import com.joy.fattyfood.utils.helper.ApiRouteConstant
import com.joy.fattyfood.utils.PreferenceUtils
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderService {

    @POST(ApiRouteConstant.routeCustomerOrderAllList)
    @FormUrlEncoded
    suspend fun fetchOrderHistories(
        @Field("customer_id") customer_id : Int,
        @Field("order_type") order_type : String,
        @Field("date") date : String,
        @Field("page") page : Int,
    ) : Response<OrderHistoriesResponse>

    @POST("api/v2/fatty/202221/lashio/main/admin/myorders/{customer_id}")
    suspend fun orderHisFoodAndParcel(
        @Path("customer_id") customer_id : Int,
        @Query("date") date : String,
        @Query("page") page : Int
    ) : Response<MyOrderHistoryResponse>

    @POST(ApiRouteConstant.routeCustomerOrderCancel)
    @FormUrlEncoded
    suspend fun foodOrderCancel(
        @Field("order_id") order_id : Int
    ) : Response<FoodOrderCancelResponse>


    @GET(ApiRouteConstant.routePaymentMethods)
    suspend fun fetchPaymentMethodList() : Response<PaymentMethodResponse>


    @POST(ApiRouteConstant.routeCustomerOrderCreate)
    @FormUrlEncoded
    suspend fun createFoodOrder(
        @Field("customer_id") customer_id: Int,
        @Field("restaurant_id") restaurant_id : Int,
        @Field("order_description") order_description : String,
        @Field("food_list") food_list : String,
        @Field("customer_address_id") customer_address_id : Int,
        @Field("delivery_fee_origin") delivery_fee_origin : Double,
        @Field("delivery_fee") delivery_fee : Double,
        @Field("item_total_price") item_total_price : Double,
        @Field("bill_total_price") bill_total_price : Double,
        @Field("customer_address_latitude") customer_address_latitude : Double,
        @Field("customer_address_longitude") customer_address_longitude : Double,
        @Field("payment_method_id") payment_method_id : Int,
        @Field("current_address") current_address : String,
        @Field("customer_address_phone") customer_address_phone : String ?= PreferenceUtils.readUserVO()?.customer_phone,
        @Field("abnormal_fee") abnormal_fee: Double
    ): Response<OrderResponse>


    @POST(ApiRouteConstant.routeFoodOrderDeliveryFee)
    @FormUrlEncoded
    suspend fun foodDeliveryFee(
        @Field("customer_address_latitude") customer_address_latitude: Double,
        @Field("customer_address_longitude") customer_address_longitude : Double,
        @Field("restaurant_id") restaurant_id : Int,
        @Field("item_total_price") item_total_price : Double
    ) : Response<FoodDeliveryFeeResponse>


    @POST(ApiRouteConstant.routeCustomerAddressList)
    @FormUrlEncoded
    suspend fun fetchCustomerAddressList(@Field("customer_id") customer_id : Int) : Response<CustomerAddressListResponse>


    @POST(ApiRouteConstant.routeParcelOrder)
    @FormUrlEncoded
    suspend fun parcelOrder(
        @Field("customer_id") userId : Int,
        @Field("parcel_zone_id") zoneID : Int
    ) : Response<ParcelOrderResponse>

    @POST(ApiRouteConstant.routeCustomerOrderClick)
    @FormUrlEncoded
    suspend fun trackFoodOrder(@Field("order_id") order_id: Int) : Response<TrackFoodOrderResponse>


    @POST("api/v2/fatty/202221/lashio/main/admin/orders/{order_id}")
    suspend fun orderDetailForReview(
        @Path("order_id") order_id: Int
    ) : Response<OrderDetailResponse>


}