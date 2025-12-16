package com.example.lmsmobile.data.model

data class QuizSummaryDTO(
    val id: Long,
    val title: String,
    val badgeSlug: String?,
    val startTime: String,
    val endTime: String,
    val submitted: Boolean
)