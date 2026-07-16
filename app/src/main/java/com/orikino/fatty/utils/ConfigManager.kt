package com.orikino.fatty.utils

import android.content.SharedPreferences
import com.orikino.fatty.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val KEY_IMAGE_BASE_URL = "KEY_IMAGE_BASE_URL"
    }

    fun save(url : String) {
        sharedPreferences.edit()
            .putString(KEY_IMAGE_BASE_URL, url)
            .apply()
    }

    fun getImageBaseUrl(): String {
        return sharedPreferences.getString(
            KEY_IMAGE_BASE_URL,
            BuildConfig.DEFAULT_IMAGE_BASE_URL
        ) ?: ""
    }
}
