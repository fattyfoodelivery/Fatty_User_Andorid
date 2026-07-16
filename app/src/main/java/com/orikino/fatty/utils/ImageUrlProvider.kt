package com.orikino.fatty.utils

import javax.inject.Inject

class ImageUrlProvider @Inject constructor(
    private val configManager: ConfigManager
) {

    fun get(path: String?): String {

        if (path.isNullOrEmpty()) return ""

        return if (path.startsWith("http")) {
            path
        } else {
            "${configManager.getImageBaseUrl()}$path"
        }
    }
}