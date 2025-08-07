package com.orikino.fatty.domain.responses

data class MyOrderHistoryResponse(
    val `data`: Data? = null,
    val message: String? = "",
    val success: Boolean? = false
) {
    data class Data(
        val `data`: MutableList<Data> ?= mutableListOf(),
        val links: Links ? = null,
        val meta: Meta ?= null
    ) {
        data class Data(
            val order_id: Int?,
            val order_type: String?,
            val customer_order_id: String?,
            val order_status_id: Int?,
            val order_status : String?,
            val currency_id: Int?,
            val order_time: String?,
            val total: Double?,
            val rider_rating: String?,
            val restaurant_rating: String?,
            val item_count: Int?,
            val from_block : String?,
            val to_block: String?,
            val restaurant_name: String?,
            val restaurant_image: String?,
        )

        data class Links(
            val first: String,
            val last: String,
            val next: String,
            val prev: Any
        )

        data class Meta(
            val current_page: Int,
            val from: Int?,
            val last_page: Int,
            val links: List<Link>,
            val path: String,
            val per_page: Int,
            val to: Int?,
            val total: Int
        ) {
            data class Link(
                val active: Boolean,
                val label: String,
                val url: String
            )
        }
    }
}