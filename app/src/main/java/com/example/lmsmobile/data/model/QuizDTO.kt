package com.example.lmsmobile.data.model

import java.util.Calendar
import java.util.Date

data class QuizDTO(
    val id: Long,
    val badgeSlug: String,
    val title: String,
    val description: String,
    val startTime: List<Int>,   // [year, month, day, hour, minute]
    val endTime: List<Int>,
    val questions: List<QuestionDTO>
) {
    // Converts startTime array to Date
    fun getStartDate(): Date {
        val cal = Calendar.getInstance()
        cal.set(startTime[0], startTime[1] - 1, startTime[2], startTime[3], startTime[4], 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    // Converts endTime array to Date
    fun getEndDate(): Date {
        val cal = Calendar.getInstance()
        cal.set(endTime[0], endTime[1] - 1, endTime[2], endTime[3], endTime[4], 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    // Optional: check if the quiz is currently active
    fun isActive(): Boolean {
        val now = Date()
        return now.after(getStartDate()) && now.before(getEndDate())
    }
}
