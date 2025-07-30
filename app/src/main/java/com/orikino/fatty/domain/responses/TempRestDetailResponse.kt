package com.orikino.fatty.domain.responses

data class TempRestDetailResponse(
    val `data`: MutableList<Data> = mutableListOf(),
    val message: String = "",
    val success: Boolean = false
) {
    data class Data(
        val additional_delivery_fee: Int = 0,
        val available_time: MutableList<AvailableTime> = mutableListOf(),
        val average_time: Int = 0,
        val city_id: Int = 0,
        val created_at: String = "",
        val define_amount: Int = 0,
        val distance: Double = 0.0,
        val is_recommend: Int = 0,
        val is_wish: Boolean = false,
        val limit_distance: Int = 0,
        val menu: MutableList<Menu> = mutableListOf(),
        val percentage: Int = 0,
        val rating: Double = 0.0,
        val restaurant_address: String = "",
        val restaurant_address_ch: String = "",
        val restaurant_address_en: String = "",
        val restaurant_address_mm: String = "",
        val restaurant_block_id: Int = 0,
        val restaurant_category_id: Int = 0,
        val restaurant_delivery_fee: Int = 0,
        val restaurant_emergency_status: Int = 0,
        val restaurant_fcm_token: String = "",
        val restaurant_id: Int = 0,
        val restaurant_image: String = "",
        val restaurant_latitude: Double = 0.0,
        val restaurant_longitude: Double = 0.0,
        val restaurant_name: String = "",
        val restaurant_name_ch: String = "",
        val restaurant_name_en: String = "",
        val restaurant_name_mm: String = "",
        val restaurant_phone: String = "",
        val restaurant_user_id: Int = 0,
        val rush_hour_time: Int = 0,
        val state_id: Int = 0,
        val updated_at: String = "",
        val wishlist: Int = 0,
        val zone_id: Int = 0
    ) {
        data class AvailableTime(
            val closing_time: String = "",
            val created_at: String = "",
            val day: String = "",
            val on_off: Int = 0,
            val opening_time: String = "",
            val restaurant_available_time_id: Int = 0,
            val restaurant_id: Int = 0,
            val updated_at: String = ""
        )

        data class Menu(
            val created_at: String = "",
            val food: MutableList<Food> = mutableListOf(),
            val food_menu_id: Int = 0,
            val food_menu_name: String = "",
            val food_menu_name_ch: String = "",
            val food_menu_name_en: String = "",
            val food_menu_name_mm: String = "",
            val restaurant_id: Int = 0,
            val updated_at: String = ""
        ) {
            data class Food(
                val food_emergency_status: Int = 0,
                val food_id: Int = 0,
                val food_image: String = "",
                val food_menu_id: Int = 0,
                val food_name_ch: String = "",
                val food_name_en: String = "",
                val food_name_mm: String = "",
                val food_price: String = "",
                val food_recommend_status: Int = 0,
                val restaurant_id: Int = 0,
                val sub_item: MutableList<SubItem> = mutableListOf()
            ) {
                data class SubItem(
                    val food_id: Int = 0,
                    val food_sub_item_id: Int = 0,
                    val option: MutableList<Option> = mutableListOf(),
                    val required_type: Int = 0,
                    val section_name_ch: String = "",
                    val section_name_en: String = "",
                    val section_name_mm: String = ""
                ) {
                    data class Option(
                        val created_at: String = "",
                        val food_id: Int = 0,
                        val food_sub_item_data_id: Int = 0,
                        val food_sub_item_id: Int = 0,
                        val food_sub_item_price: String = "",
                        val instock: Int = 0,
                        val item_name: String = "",
                        val item_name_ch: String = "",
                        val item_name_en: String = "",
                        val item_name_mm: String = "",
                        val restaurant_id: Int = 0,
                        val updated_at: String = ""
                    )
                }
            }
        }
    }
}