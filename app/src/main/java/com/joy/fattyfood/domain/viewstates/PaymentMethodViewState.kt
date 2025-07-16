package com.joy.fattyfood.domain.viewstates

import com.joy.fattyfood.domain.responses.*

sealed class PaymentMethodViewState{
    object OnLoadingPaymentMethod :  com.joy.fattyfood.domain.viewstates.PaymentMethodViewState()
    data class OnSuccessPaymentMethod(val data : PaymentMethodResponse) : com.joy.fattyfood.domain.viewstates.PaymentMethodViewState()
    data class OnFailPaymentMethod(val message : String) : com.joy.fattyfood.domain.viewstates.PaymentMethodViewState()
}
