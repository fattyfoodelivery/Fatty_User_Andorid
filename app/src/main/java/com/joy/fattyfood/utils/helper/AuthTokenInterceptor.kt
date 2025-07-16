package com.joy.fattyfood.utils.helper

import com.joy.fattyfood.utils.PreferenceUtils
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthTokenInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val deviceToken = PreferenceUtils.readDeviceToken()
        val language = PreferenceUtils.readLanguage()
        val customerId = PreferenceUtils.readUserVO().customer_id.toString()
        val zoneId = PreferenceUtils.readZoneId().toString()
        val currencyId = PreferenceUtils.readCurrCurrency()?.currency_id.toString()
        //val currencyId = PreferenceUtils.readCurrencyId()?.currency_id.toString()


        val requestBuilder = chain.request().newBuilder()

        deviceToken?.let { requestBuilder.addHeader("device-id", it) }
        language?.let { requestBuilder.addHeader("language", it) }
        customerId.let { requestBuilder.addHeader("customer-id", it) }
        zoneId.let { requestBuilder.addHeader("zone-id", it) }
        currencyId.let { requestBuilder.addHeader("currency-id", it ) }

        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }
}