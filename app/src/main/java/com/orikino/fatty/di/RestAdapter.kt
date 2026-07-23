package com.orikino.fatty.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.orikino.fatty.data.interceptors.InternetConnectivityInterceptor
import com.orikino.fatty.utils.helper.AuthTokenInterceptor
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

fun createRetrofitClient(baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
    val moshi = Moshi.Builder().build()
    return Retrofit.Builder().baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
}

fun createOkHttpClient(
    context: Context,
    chuckerInterceptor: ChuckerInterceptor,
): OkHttpClient {
    val builder = OkHttpClient.Builder()
    builder.connectTimeout(60, TimeUnit.SECONDS)
    builder.writeTimeout(60, TimeUnit.SECONDS)
    builder.readTimeout(60, TimeUnit.SECONDS)
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    builder.networkInterceptors().add(httpLoggingInterceptor)
    builder.addInterceptor(AuthTokenInterceptor())
    builder.addInterceptor(InternetConnectivityInterceptor(context))
    builder.addInterceptor(chuckerInterceptor)
    return builder.build()
}
