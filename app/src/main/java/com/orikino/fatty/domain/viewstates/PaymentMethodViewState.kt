package com.orikino.fatty.domain.viewstates

import com.orikino.fatty.domain.responses.*

sealed class PaymentMethodViewState{
    object OnLoadingPaymentMethod :  com.orikino.fatty.domain.viewstates.PaymentMethodViewState()
    data class OnSuccessPaymentMethod(val data : PaymentMethodResponse) : com.orikino.fatty.domain.viewstates.PaymentMethodViewState()
    data class OnFailPaymentMethod(val message : String) : com.orikino.fatty.domain.viewstates.PaymentMethodViewState()
}
