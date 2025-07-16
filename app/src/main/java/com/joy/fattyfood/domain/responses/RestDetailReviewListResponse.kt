package com.joy.fattyfood.domain.responses

data class RestDetailReviewListResponse(
    val `data`: Data? = null,
    val message: String? = "",
    val status: Int? = 0,
    val success: Boolean ? = false
) {
    data class Data(
        val rating: Int? = 0,
        val rating_count: Int? = 0,
        val reviews: Reviews? = null
    ) {
        data class Reviews(
            val `data`: MutableList<Data>? = mutableListOf(),
            val links: Links? = null,
            val meta: Meta? = null
        ) {
            data class Data(
                val comment: String ? = "",
                val customer_name: String? = "",
                val customer_profile: String? = "",
                val id: Int? = 0,
                val options: MutableList<String>?= mutableListOf(),
                val order_id: Int? = 0,
                val review_date: String? = "",
                val star: String? = ""
            )

            data class Links(
                val first: String? = "",
                val last: String? ="",
                val next: String? = "",
                val prev: String? = ""
            )

            data class Meta(
                val current_page: Int? = 0,
                val from: Int? = 0,
                val last_page: Int? = 0,
                val links: MutableList<Link>? = mutableListOf(),
                val path: String? = "",
                val per_page: Int?= 0,
                val to: Int?= 0,
                val total: Int? = 0
            ) {
                data class Link(
                    val active: Boolean?= false,
                    val label: String? = "",
                    val url: String? = ""
                )
            }
        }
    }
}