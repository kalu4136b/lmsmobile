package com.example.lmsmobile.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Routes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard/{studentIndex}/{studentName}"

    fun dashboardRoute(index: String, name: String): String {
        val encodedName = URLEncoder.encode(name.trim(), StandardCharsets.UTF_8.name())
        return "dashboard/${index.trim()}/$encodedName"
    }
}