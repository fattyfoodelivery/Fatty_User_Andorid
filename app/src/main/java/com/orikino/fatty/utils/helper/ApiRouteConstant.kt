package com.orikino.fatty.utils.helper

object ApiRouteConstant {
    const val NEWPNS = "" // "newpns/"

    const val routeAboutApp = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/application/abouts"
    const val routeCurrencyList = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/currency/list"
    const val routePrivacyPolicy = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/application/customers/privacy"
    const val routeTermAndCondition = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/application/customers/termsandconditions"
    const val routeLogoutApp = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/logout"
    const val routeHome = NEWPNS + "api/v2/fatty/customer/home_page"

    const val routeCustomerAddress = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/address/create"
    const val routeUpdateCurrentAddress = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/address/update"

    const val routeCategoryByCategoryID = NEWPNS + "api/v2/fatty/customer/category_data"
    const val routeCustomerWishList = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/wishlists"
    const val routeAdsEngagement = NEWPNS + "api/v2/fatty/customer/make_engagement"
    const val routeCategoryList = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/category/list"
    const val routeHelpCenter = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/application/support_centers"

    const val routeUpdateUserLocation = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/location"
    const val routeVersionCheck = NEWPNS + "api/v1/fatty/main/admin/android/version/check"
    const val routeOnBoarding = NEWPNS + "api/v2/fatty/customer/onboading_page"
    const val routeInboxNotification = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers_notifications"
    const val routeNotificationMenuTypeList = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/notifications/menus"
    const val routeOrderReview = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/orders/reviews"
    const val routeRatingValue = NEWPNS + "api/v2/fatty/202221/lashio/main/admin/rating-values"
    const val routeAdminRating = NEWPNS + "api/v2/fatty/202221/lashio/main/admin/rating"
    const val routeRequestOTP = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/request_otp"
    const val routeResendRequestOTP = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/resend_request_otp"


    const val routeCustomerAddressList = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/address/list"
    const val routeCustomerAddressDelete = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/address/delete"
    const val routeCustomerDefaultAddress = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/address/default"
    const val routeParcelDefaultAddressList = NEWPNS + "api/v2/fatty/202221/lashio/main/admin/customers/parcels/default_address/list"
    const val routeParcelDefaultAddressStore = NEWPNS + "api/v2/fatty/202221/lashio/main/admin/customers/parcels/default_address/store"
    const val routeParcelDefaultAddressUpdate = NEWPNS + "api/v2/fatty/202221/lashio/main/admin/customers/parcels/default_address/update"
    const val routeParcelDefaultAddressDestroy = NEWPNS + "api/v2/fatty/202221/lashio/main/admin/customers/parcels/default_address/destroy"
    const val routeParcelDefaultAddressCreate = NEWPNS + "api/v2/fatty/202221/lashio/main/admin/customers/parcels/default_address/default/create"

    const val routePaymentMethods = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/order/payment/lists"
    const val routeCustomerOrderCreate = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/orders/create"
    const val routeFoodOrderDeliveryFee = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/food/orders/delivery/fee"

    const val routeCustomerOrderAllList = NEWPNS + "api/v2/fatty/202221/lashio/main/admin/customers/orders/all_lists"
    const val routeCustomerOrderCancel = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/orders/cancel"

    const val routeParcelStateCity = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/parcels/states"
    const val routeCustomerParcelTypeList = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/parcels/type/list"
    const val routeCustomerParcelExtraList = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/parcels/extra/list"
    const val routeCustomerParcelOrderTotalEstimate = NEWPNS + "api/v2/fatty/202221/lashio/main/admin/customers/parcels/orders/total_estimate"
    const val routeCustomerParcelOrderStore = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/parcels/orders/store"
    const val routeParcelChooseAddress = NEWPNS + "api/v2/fatty/202221/lashio/main/admin/parcels/choose_address"

    const val routeCustomerRecommendedList = NEWPNS + "api/v2/fatty/customer/recommend_list"
    const val routeRestaurantDetail = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/click/restaurant/data"

    const val routeRestaurantFoodTypeByMenuID = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/click/menu/data"
    const val routeFoodMenuByRestaurant = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/click/menu/data"
    const val routeRestaurantDetailReviewList = "api/v1/fatty/202221/lashio/main/admin/reviews/restaurant"

    const val routeSearchFoodAndRestaurant = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/search"
    const val routeFilterRestaurant = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/filter"
    const val routeCategoryFilterSearch =  "api/v2/fatty/202221/lashio/main/admin/categories/filter"


    const val routeOrderDetail = NEWPNS + "api/v2/fatty/202221/lashio/main/admin/orders/{order_id}"
    const val routeCustomerOrderClick = NEWPNS + "api/v2/fatty/202221/lashio/main/admin/customer/orders/click"
    const val routeRiderLocation = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/rider/location"

    const val routeAppTutorial = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/application/tutorials"

    const val routeUpdateCustomerInfo = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/update"


    const val routeVerifyOTP = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/verify_otp"
    const val routeCustomerLogin = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/customers/login"

    const val routeWishList = NEWPNS + "api/v1/fatty/202221/lashio/main/admin/wishlist/list"

    const val routeTopRelatedCategory = "api/v2/fatty/202221/lashio/main/admin/restaurants/top-rated"
    const val routeUserNoti = "api/v2/fatty/202221/lashio/main/admin/notifications/user"
    const val routeParcelOrder = "api/v2/fatty/202221/lashio/main/admin/one_click_order"
    const val routeSystemNoti = "api/v2/fatty/202221/lashio/main/admin/notifications/system"

    const val routeReadNoti = "api/v2/fatty/202221/lashio/main/admin/notifications/make_as_read"

    const val routeCustomerOrderHistory = NEWPNS + "api/v2/fatty/202221/lashio/main/admin/customers/orders/all_lists"

    const val routeFeedbackDeliveryToRider = "api/v1/fatty/202221/lashio/main/admin/customers/orders/reviews"

    const val routeDeleteAccount = "api/v1/fatty/202221/lashio/main/admin/customers/destroy"

    /*
    * interface DeliveryRatingApi {
    //@POST("api/v1/fatty/202221/lashio/main/admin/customers/orders/reviews")
    @POST(ApiRouteConstant.routeOrderReview)
    @FormUrlEncoded
    suspend fun feedbackDeliveryService(
        @Field("order_id") order_id: Int,
        @Field("rating") rating: Int,
        @Field("description") description: String
    ): DeliverRatingResponse

    //@POST("api/v2/fatty/202221/lashio/main/admin/rating-values")
    @POST(ApiRouteConstant.routeRatingValue)
    suspend fun getRatingValues(): RatingValueResponse

    //@POST("api/v2/fatty/202221/lashio/main/admin/rating")
    @POST(ApiRouteConstant.routeAdminRating)
    @FormUrlEncoded
    suspend fun rating(
        @Field("rating_type") rating_type: String,
        @Field("order_id") order_id: Int,
        @Field("star") rating: String,
        @Field("options") options: String,
        @Field("comment") description: String,
        @Field("option_locale") locale: String
    ): RatingResponse



} */

}