package com.orikino.fatty.data.interceptors

import android.content.Context
import com.orikino.fatty.data.exceptions.AppHttpException
import com.orikino.fatty.utils.helper.isNetworkAvailable
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class InternetConnectivityInterceptor @Inject constructor(
    context: Context
): Interceptor {
    private val applicationContext: Context = context.applicationContext

    private val isConnected: Boolean
        get() {
            return isNetworkAvailable(applicationContext)//applicationContext.isConnected()
        }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        if (!isConnected) {
            throw AppHttpException("No Internet connection")
        }
        return chain.proceed(originalRequest)
    }
}