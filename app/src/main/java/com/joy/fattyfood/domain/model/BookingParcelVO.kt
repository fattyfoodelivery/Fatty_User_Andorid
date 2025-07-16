package com.joy.fattyfood.domain.model

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class BookingParcelVO(
    @SerializedName("customer_id")
    var customer_id: RequestBody,
    @SerializedName("from_sender_name")
    var from_sender_name: RequestBody,
    @SerializedName("from_sender_phone")
    var from_sender_phone: RequestBody,
    @SerializedName("from_pickup_address")
    var from_pickup_address: RequestBody,
    @SerializedName("from_pickup_latitude")
    var from_pickup_latitude: RequestBody,
    @SerializedName("from_pickup_longitude")
    var from_pickup_longitude: RequestBody,
    @SerializedName("from_pickup_note")
    var from_pickup_note : RequestBody,
    @SerializedName("to_recipent_name")
    var to_recipent_name: RequestBody,
    @SerializedName("to_recipent_phone")
    var to_recipent_phone: RequestBody,
    @SerializedName("to_drop_address")
    var to_drop_address: RequestBody,
    @SerializedName("to_drop_latitude")
    var to_drop_latitude: RequestBody,
    @SerializedName("to_drop_longitude")
    var to_drop_longitude: RequestBody,
    @SerializedName("to_drop_note")
    var to_drop_note : RequestBody,
    @SerializedName("parcel_type_id")
    var parcel_type_id: RequestBody,
    @SerializedName("total_estimated_weight")
    var total_estimated_weight: RequestBody,
    @SerializedName("parcel_order_note")
    var parcel_order_note: RequestBody,
    @SerializedName("parcel_extra_cover_id")
    var parcel_extra_cover_id: RequestBody,
    @SerializedName("item_qty")
    var item_qty: RequestBody,
    @SerializedName("parcel_image_list[]")
    var parcel_image_list: MutableList<MultipartBody.Part>,
    @SerializedName("payment_method_id")
    var payment_method_id: RequestBody,
    @SerializedName("bill_total_price")
    var bill_total_price: RequestBody,
    @SerializedName("zone_id")
    var zone_id: RequestBody,
    @SerializedName("city_id")
    var city_id: RequestBody,
    @SerializedName("delivery_fee")
    var delivery_fee: RequestBody,
    @SerializedName("delivery_fee_currency")
    var delivery_fee_currency: RequestBody,
    @SerializedName("extra_coverage")
    var extra_coverage: RequestBody,
    @SerializedName("extra_coverage_currency")
    var extra_coverage_currency: RequestBody,
    @SerializedName("total_estimated_currency")
    var total_estimated_currency: RequestBody,
    @SerializedName("exchange_rate")
    var exchange_rate: RequestBody,
    @SerializedName("from_parcel_city_id")
    var from_parcel_city_id: RequestBody,
    @SerializedName("to_parcel_city_id")
    var to_parcel_city_id: RequestBody,
    @SerializedName("abnormal_fee")
    var abnormal_fee: RequestBody
)
