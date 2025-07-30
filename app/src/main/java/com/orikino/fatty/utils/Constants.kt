package com.orikino.fatty.utils

import android.content.Context

object Constants {
    const val CONNECTION_ISSUE = "Connection Issue"
    const val SERVER_ISSUE = "Server Error"
    const val ANOTHER_LOGIN = "Another Login"
    const val DENIED = "Denied"
    const val SOMETHING_WRONG = "Something Wrong"
    const val FAILED = "Failed"
    const val EMPTY_RESPONSE = "Empty Response Body"

    const val CHECK_RATING_ACTION = "check_rating_action"
    fun setPreferenceBoolean(context: Context, key: String, value: Boolean) {
        val sharedPreference = context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getPreferenceBoolean(context: Context, key: String): Boolean {
        val sharedPreference = context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        return sharedPreference.getBoolean(key, false)
    }
    fun setPreferenceInt(context: Context, key: String, value: Int) {
        val sharedPreference = context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putInt(key, value)
        editor.apply()
    }
    fun getPreferenceInt(context: Context, key: String): Int {
        val sharedPreference = context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        return sharedPreference.getInt(key, 0)
    }
}
