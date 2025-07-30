package com.orikino.fatty.data.repository

import com.orikino.fatty.domain.responses.*
import com.orikino.fatty.domain.responses.FoodOrderCancelResponse
import retrofit2.Response

interface OrderRepository {
    suspend fun fetchOrderHistoryList(
        customer_id: Int,
        order_type : String,
        date: String,
        page: Int
    ): Response<OrderHistoriesResponse>

    suspend fun foodOrderCancel(
        order_id : Int
    ) : Response<FoodOrderCancelResponse>


    suspend fun myOrderHistory(
        customer_id: Int,
        date: String,
        page: Int
    ) : Response<MyOrderHistoryResponse>
    suspend fun fetchPaymentMethods() : Response<PaymentMethodResponse>

    suspend fun createFoodOrder(
        customer_id: Int,
        restaurant_id : Int,
        order_description : String,
        food_list : String,
        customer_address_id : Int,
        delivery_fee_origin : Double,
        delivery_fee : Double,
        item_total_price : Double,
        bill_total_price : Double,
        customer_address_latitude : Double,
        customer_address_longitude : Double,
        payment_method_id : Int,
        current_address : String,
        customer_address_phone : String,
        abnormal_fee: Double
    ) : Response<OrderResponse>

    suspend fun foodDeliveryFee(
        latitude : Double,
        longitude : Double,
        restaurant_id : Int,
        item_total_price : Double
    ) : Response<FoodDeliveryFeeResponse>

    suspend fun fetchCustomerAddressList(
        customer_id : Int
    ) : Response<CustomerAddressListResponse>

    suspend fun trackFoodOrder(order_id : Int) : Response<TrackFoodOrderResponse>

    suspend fun orderDetailReview(order_id: Int) : Response<OrderDetailResponse>

    /*suspend fun trackFoodOrder(order_id : Int) : Response<TrackFoodOrderResponse>
    suspend fun fetchRiderLocation(rider_id : Int) : Response<RiderLocationResponse>

    suspend fun checkRatingAndReview(order_id: String): Response<OrderDetailResponse>

    suspend fun fetchPaymentMethod() : Response<PaymentMethodResponse>
    suspend fun createFoodOrder(
        customer_id: Int,
        restaurant_id : Int,
        order_description : String,
        food_list : String,
        customer_address_id : Int,
        delivery_fee_origin : Double,
        delivery_fee : Double,
        item_total_price : Double,
        bill_total_price : Double,
        customer_address_latitude : Double,
        customer_address_longitude : Double,
        payment_method_id : Int,
        current_address : String,
        customer_address_phone : String,
        abnormal_fee: Double
    ) : Response<OrderResponse>

    suspend fun foodDeliveryFee(
        lat : Double,
        lng : Double,
        restaurant_id: Int,
        item_total_price: Double
    ) : Response<FoodDeliveryFeeResponse>
    suspend fun trackParcelOrder(order_id : Int) : Response<TrackFoodOrderResponse>*/

}