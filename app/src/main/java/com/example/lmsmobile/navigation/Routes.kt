package com.example.lmsmobile.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Routes {

    // Static routes
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard/{studentIndex}/{studentName}/{degreeId}"
    const val TASK_SCHEDULE = "tasks/{degreeId}"
    const val RESULTS = "results/{studentIndex}/{studentName}/{degreeId}"
    const val PROFILE = "profile_screen/{indexNumber}"
    const val QUIZ = "quiz_screen/{indexNumber}"

    // 🔹 New Quiz List route
    const val QUIZ_LIST = "quiz_list/{studentIndex}"

    fun dashboardRoute(index: String, name: String, degreeId: Long): String {
        val encodedName = URLEncoder.encode(name.trim(), StandardCharsets.UTF_8.name())
        return "dashboard/${index.trim()}/$encodedName/$degreeId"
    }

    fun taskScheduleRoute(degreeId: Long): String {
        return "tasks/$degreeId"
    }

    fun resultsRoute(index: String, name: String, degreeId: Long): String {
        val encodedName = URLEncoder.encode(name.trim(), StandardCharsets.UTF_8.name())
        return "results/${index.trim()}/$encodedName/$degreeId"
    }

    fun profileRoute(index: String): String {
        return "profile_screen/${index.trim()}"
    }

    fun quizRoute(index: String): String {
        return "quiz_screen/${index.trim()}"
    }

    // Helper for quiz list navigation
    fun quizListRoute(index: String): String {
        return "quiz_list/${index.trim()}"
    }

    const val SUBJECTS = "subjects/{studentIndex}"
    fun subjectsRoute(index: String) = "subjects/$index"

    const val NOTE = "note/{subjectId}/{subjectName}"
    fun noteRoute(subjectId: Long, subjectName: String): String {
        val encoded = URLEncoder.encode(subjectName, StandardCharsets.UTF_8.name())
        return "note/$subjectId/$encoded"
    }
}