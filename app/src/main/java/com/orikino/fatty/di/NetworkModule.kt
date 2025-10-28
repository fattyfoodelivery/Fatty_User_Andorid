package com.orikino.fatty.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Module
    @InstallIn(SingletonComponent::class)
    object Provider {

        @Provides
        @Named("MVVMClient")
        @Singleton
        fun provideMvvmClientRetrofit(@Named("ClientOkhttp") client: OkHttpClient): Retrofit {
            return createRetrofitClient(
               "https://boadmin.foodelivery.click/"
                //"https://boadmin.fattyfooddeli.com/"
                //"https://dev.fattyfooddeli.com/"
               /* if (BuildConfig.DEBUG)
                    "http://139.59.227.117:8080"   // Testing
                else
                    "http://157.245.144.179"       // Production
*/
                , client)
        }

        @Provides
        @Named("ClientOkhttp")
        @Singleton
        fun provideClientOkhttpClient(
            @ApplicationContext context: Context,
            chuckerInterceptor: ChuckerInterceptor,
        ): OkHttpClient {
            return createOkHttpClient(context,chuckerInterceptor)
        }

        @Provides
        @Singleton
        fun provideChuckerInterceptor(@ApplicationContext context: Context): ChuckerInterceptor {
            val chuckerCollector = ChuckerCollector(
                context = context,
                showNotification = true,
                retentionPeriod = RetentionManager.Period.ONE_HOUR
            )
            return ChuckerInterceptor.Builder(context)
                .collector(chuckerCollector)
                .maxContentLength(250_000L)
                .alwaysReadResponseBody(true)
                .build()
        }
    }
}