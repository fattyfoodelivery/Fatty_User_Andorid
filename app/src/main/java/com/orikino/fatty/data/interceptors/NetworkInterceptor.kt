package com.orikino.fatty.data.interceptors
import java.io.IOException

class NetworkExceptionInterceptor :  okhttp3.Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
        val response = chain.proceed(chain.request())
        when(response.isSuccessful){
            true -> return response
            else -> {
                throw NetworksException(response.body, response.code)
            }
        }
    }
}

data class NetworksException constructor(
    val errorBody: okhttp3.ResponseBody?,
    val errorCode: Int?,
) :IOException(errorCode.toString())