package com.orikino.fatty.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
/*import androidx.lifecycle.ProcessLifecycleOwner
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.search.MapboxSearchSdk*/
import com.orikino.fatty.utils.PreferenceUtils
import com.orikino.fatty.utils.helper.correctLocale
import com.orikino.fatty.utils.helper.isLocationEnabled
import dagger.hilt.android.HiltAndroidApp
import io.paperdb.Paper
import me.pushy.sdk.Pushy

@HiltAndroidApp
class FattyApp : Application() , LifecycleObserver {

    companion object {
        val TAG = FattyApp::class.java.simpleName
        private lateinit var instance: FattyApp

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        fun getInstance(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        Paper.init(applicationContext)
        instance = this

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        onAppBackgrounded()

        Pushy.listen(applicationContext)
        //GoogleMap.getInstance(applicationContext, "pk.eyJ1IjoiZmF0dHlmb29kIiwiYSI6ImNsc3ZwcjRiajFuNXQya3FrZnRlanM5dDEifQ.Z2qH4uwufB38l75-YaMCng")
        //MapOptions.Builder().apply { "pk.eyJ1IjoiZmF0dHlmb29kIiwiYSI6ImNsc3ZwcjRiajFuNXQya3FrZnRlanM5dDEifQ.Z2qH4uwufB38l75-YaMCng" }
        /*pk.eyJ1IjoiZmF0dHlmb29kIiwiYSI6ImNsc3ZwcjRiajFuNXQya3FrZnRlanM5dDEifQ.Z2qH4uwufB38l75-YaMCng*/
        /*MapboxSearchSdk.initialize(
            application = this,
            accessToken = "pk.eyJ1IjoiZmF0dHlmb29kIiwiYSI6ImNsc3ZwcjRiajFuNXQya3FrZnRlanM5dDEifQ.Z2qH4uwufB38l75-YaMCng",
            locationEngine = LocationEngineProvider.getBestLocationEngine(this)
        )*/
        correctLocale()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        PreferenceUtils.isGpsEnable.postValue(applicationContext.isLocationEnabled())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        PreferenceUtils.isBackground = true
        Log.d("Awww", "App in background")
    }
}
