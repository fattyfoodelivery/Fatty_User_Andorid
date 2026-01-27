package com.orikino.fatty.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale


class LocaleHelper {
    fun onAttach(context: Context?): Context? {
        val lang = PreferenceUtils.readLanguage()
        return setLocale(context, lang!!)
    }

    fun onAttach(context: Context, defaultLanguage: String?): Context? {
        val lang = PreferenceUtils.readLanguage()
        return setLocale(context, lang!!)
    }

    fun getLanguage(context: Context?): String? {
        return PreferenceUtils.readLanguage()
    }

    fun setLocale(context: Context?, language: String): Context? {
        PreferenceUtils.writeLanguage(language)

        return updateResources(context, language)

    }

    private fun updateResources(context: Context?, language: String): Context? {
        val mLocale = Locale(language)
        Locale.setDefault(mLocale)

        val resources = context?.resources
        val config = resources?.configuration
        config?.setLocale(mLocale)

        // Update the configuration for older versions
        resources?.updateConfiguration(config, resources.displayMetrics)
        return context?.createConfigurationContext(config!!)
    }

    @Suppress("deprecation")
    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale: Locale = Locale(language)
        Locale.setDefault(locale)

        val resources: Resources = context.resources

        val configuration: Configuration = resources.configuration
        configuration.locale = locale
        configuration.setLayoutDirection(locale)

        resources.updateConfiguration(configuration, resources.displayMetrics)

        return context
    }
}