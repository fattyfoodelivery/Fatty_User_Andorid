package com.joy.fattyfood.domain.viewstates

import com.joy.fattyfood.domain.responses.*

sealed class DeliveryRatingViewState {
    object OnLoadingDeliveryRating : DeliveryRatingViewState()
    data class OnSuccessDeliveryRating(val data: DeliverRatingResponse) : DeliveryRatingViewState()
    data class OnFailDeliveryRating(val message: String) : DeliveryRatingViewState()

    object OnLoadingRating : DeliveryRatingViewState()

    data class OnSuccessRating(val data: RatingResponse) : DeliveryRatingViewState()

    data class OnFailRating(val message: String) : DeliveryRatingViewState()

    object OnLoadingRatingValue : DeliveryRatingViewState()

    data class OnSuccessRatingValue(val data: RatingValueResponse) : DeliveryRatingViewState()

    data class OnFailRatingValue(val message: String) : DeliveryRatingViewState()
}
sealed class HelpCenterViewState {
    object OnLoadingHelpCenter : HelpCenterViewState()
    data class OnSuccessHelpCenter(val data: AppHelpCenterResponse) : HelpCenterViewState()
    data class OnFailHelpCenter(val message: String) : HelpCenterViewState()
}



