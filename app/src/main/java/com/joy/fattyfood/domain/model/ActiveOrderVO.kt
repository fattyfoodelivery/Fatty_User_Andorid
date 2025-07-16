package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName

data class ActiveOrderVO(
    @SerializedName("order_id")
    var order_id : Int = 0,
    @SerializedName("customer_order_id")
    var customer_order_id : String = "",
    @SerializedName("customer_booking_id")
    var customer_booking_id : String = "",
    @SerializedName("customer_address_id")
    var customer_address_id : Int ?= null,
    @SerializedName("bill_total_price")
    var bill_total_price : Double =0.0,
    @SerializedName("item_total_price")
    var item_total_price : Double ?= null,
    @SerializedName("estimated_start_time")
    var estimated_start_time : String ?= null,
    @SerializedName("estimated_end_time")
    var estimated_end_time : String ?= null,
    @SerializedName("delivery_fee")
    var delivery_fee : Double  = 0.0,
    @SerializedName("currency_id")
    var currency_id : Int  = 0,
    @SerializedName("exchange_rate")
    var exchange_rate : Double = 0.0,
    @SerializedName("currency_type")
    var currency_type : String = "",
    @SerializedName("order_description")
    var order_description : String ?= null,
    @SerializedName("is_review_status")
    var is_review_status : Int = 0,
    @SerializedName("payment_method")
    var payment_method : PaymentMethodVO? = PaymentMethodVO(),
    @SerializedName("order_status")
    var order_status : OrderStatusVO? = OrderStatusVO(),
    @SerializedName("restaurant")
    var restaurant : RecommendRestaurantVO ?= RecommendRestaurantVO(),

    @SerializedName("customer")
    var customer: OrderCustomer ? = OrderCustomer(),

    @SerializedName("rider_restaurant_distance")
    var rider_restaurant_distance : Double ? = 0.0,
    /*@SerializedName("customer_address_latitude")
    var customer_address_latitude : Double ? =0.0,
    @SerializedName("customer_address_longitude")
    var customer_address_longitude : Double ? =0.0,
    @SerializedName("customer_address")
    var customer_address : CustomerCurrentAddressVO ?= null,
    @SerializedName("customer_address_phone")
    var customer_address_phone : String ?= null,*/
    @SerializedName("customer_address_phone")
    var customer_address_phone : String ?= null,
    @SerializedName("current_address")
    var current_address :String ?= null,
    @SerializedName("foods")
    var foods : MutableList<FoodVO> = mutableListOf(),
    @SerializedName("rider")
    var rider : RiderVO ?= RiderVO(),
    @SerializedName("item_qty")
    var item_qty : Int ?= null,
    @SerializedName("from_sender_name")
    var from_sender_name : String ?= null,
    @SerializedName("from_sender_phone")
    var from_sender_phone : String ?= null,
    @SerializedName("from_pickup_address")
    var from_pickup_address : String ?=null,
    @SerializedName("from_pickup_latitude")
    var from_pickup_latitude : Double ? = 0.0,
    @SerializedName("from_pickup_longitude")
    var from_pickup_longitude : Double ? = 0.0,
    @SerializedName("from_pickup_note")
    var from_pickup_note : String ?=null,
    @SerializedName("to_drop_latitude")
    var to_drop_latitude : Double ? = 0.0,
    @SerializedName("to_drop_longitude")
    var to_drop_longitude : Double ? = 0.0,
    @SerializedName("to_drop_note")
    var to_drop_note : String ?=null,
    @SerializedName("to_recipent_name")
    var to_recipent_name : String = "",
    @SerializedName("to_recipent_phone")
    var to_recipent_phone : String ?= null,
    @SerializedName("to_drop_address")
    var to_drop_address : String ?= null,
    @SerializedName("from_parcel_city_name")
    var from_parcel_city_name : String ?= null,
    @SerializedName("to_parcel_city_name")
    var to_parcel_city_name : String ?= null,
    @SerializedName("total_estimated_weight")
    var total_estimated_weight : Double ?= null,
    @SerializedName("parcel_type")
    var parcel_type : ParcelTypeVO ?= null,
    @SerializedName("parcel_order_note")
    var parcel_order_note : String ?= null,
    @SerializedName("order_date")
    var order_date : String ?= null,
    @SerializedName("order_time")
    var order_time : String ?= null,
    @SerializedName("rider_accept_time")
    var rider_accept_time : String ?= null,
    @SerializedName("parcel_extra")
    var parcel_extra : ParcelExtraVO ?= ParcelExtraVO(),
    @SerializedName("parcel_images")
    var parcel_images : MutableList<ParcelImageVO> = mutableListOf(),
    @SerializedName("created_at")
    var created_at : String = "",
    @SerializedName("refund_amount")
    var refund_amount :Int = 0,
    @SerializedName("have_review")
    var have_review : Int = 0,
    @SerializedName("is_abnormal")
    var is_abnormal: Boolean? = false,
    @SerializedName("rider_review")
    var rider_review: RatingDetailVO? = RatingDetailVO(),
    @SerializedName("restaurant_review")
    var restaurant_review: RatingDetailVO? = RatingDetailVO(),

    )


data class CreateNewFoodOrderVO(
    @SerializedName("order_id")
    var order_id : Int = 0,
    @SerializedName("customer_order_id")
    var customer_order_id : String = "",
    @SerializedName("customer_booking_id")
    var customer_booking_id : String = "",
    @SerializedName("customer_address_id")
    var customer_address_id : Int ?= null,
    @SerializedName("bill_total_price")
    var bill_total_price : Double =0.0,
    @SerializedName("item_total_price")
    var item_total_price : Double ?= null,
    @SerializedName("estimated_start_time")
    var estimated_start_time : String ?= null,
    @SerializedName("estimated_end_time")
    var estimated_end_time : String ?= null,
    @SerializedName("delivery_fee")
    var delivery_fee : Double  = 0.0,
    @SerializedName("currency_id")
    var currency_id : Int  = 0,
    @SerializedName("exchange_rate")
    var exchange_rate : Double = 0.0,
    @SerializedName("currency_type")
    var currency_type : String = "",
    @SerializedName("order_description")
    var order_description : String ?= null,
    @SerializedName("is_review_status")
    var is_review_status : Int = 0,
    @SerializedName("payment_method")
    var payment_method : PaymentMethodVO = PaymentMethodVO(),
    @SerializedName("order_status")
    var order_status : OrderStatusVO = OrderStatusVO(),
    @SerializedName("restaurant")
    var restaurant : RecommendRestaurantVO ?= null,

    @SerializedName("customer")
    var customerVO: OrderCustomer ? = OrderCustomer(),

    @SerializedName("rider_restaurant_distance")
    var rider_restaurant_distance : Double ? = 0.0,
    /*@SerializedName("customer_address_latitude")
    var customer_address_latitude : Double ? =0.0,
    @SerializedName("customer_address_longitude")
    var customer_address_longitude : Double ? =0.0,
    @SerializedName("customer_address")
    var customer_address : CustomerCurrentAddressVO ?= null,
    @SerializedName("customer_address_phone")
    var customer_address_phone : String ?= null,*/
    @SerializedName("customer_address_phone")
    var customer_address_phone : String ?= null,
    @SerializedName("current_address")
    var current_address :String ?= null,
    @SerializedName("foods")
    var foods : MutableList<FoodVO> = mutableListOf(),
    @SerializedName("rider")
    var rider : RiderVO ?= null,
    @SerializedName("item_qty")
    var item_qty : Int ?= null,
    @SerializedName("from_sender_name")
    var from_sender_name : String ?= null,
    @SerializedName("from_sender_phone")
    var from_sender_phone : String ?= null,
    @SerializedName("from_pickup_address")
    var from_pickup_address : String ?=null,
    @SerializedName("from_pickup_latitude")
    var from_pickup_latitude : Double ? = 0.0,
    @SerializedName("from_pickup_longitude")
    var from_pickup_longitude : Double ? = 0.0,
    @SerializedName("from_pickup_note")
    var from_pickup_note : String ?=null,
    @SerializedName("to_drop_latitude")
    var to_drop_latitude : Double ? = 0.0,
    @SerializedName("to_drop_longitude")
    var to_drop_longitude : Double ? = 0.0,
    @SerializedName("to_drop_note")
    var to_drop_note : String ?=null,
    @SerializedName("to_recipent_name")
    var to_recipent_name : String = "",
    @SerializedName("to_recipent_phone")
    var to_recipent_phone : String ?= null,
    @SerializedName("to_drop_address")
    var to_drop_address : String ?= null,
    @SerializedName("from_parcel_city_name")
    var from_parcel_city_name : String ?= null,
    @SerializedName("to_parcel_city_name")
    var to_parcel_city_name : String ?= null,
    @SerializedName("total_estimated_weight")
    var total_estimated_weight : Double ?= null,
    @SerializedName("parcel_type")
    var parcel_type : ParcelTypeVO ?= null,
    @SerializedName("parcel_order_note")
    var parcel_order_note : String ?= null,
    @SerializedName("order_date")
    var order_date : String ?= null,
    @SerializedName("order_time")
    var order_time : String ?= null,
    @SerializedName("parcel_extra")
    var parcel_extra : ParcelExtraVO ?=null,
    @SerializedName("parcel_images")
    var parcel_images : MutableList<ParcelImageVO> = mutableListOf(),
    @SerializedName("created_at")
    var created_at : String = "",
    @SerializedName("refund_amount")
    var refund_amount :Int = 0,
    @SerializedName("have_review")
    var have_review : Int = 0,
    @SerializedName("is_abnormal")
    var is_abnormal: Double? = 0.0
)


data class OrderCustomer(
    val customer_name : String? = "",
    val customer_phone : String? = "",

)
