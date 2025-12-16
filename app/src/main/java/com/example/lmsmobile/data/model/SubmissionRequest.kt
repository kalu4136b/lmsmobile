package com.example.lmsmobile.data.model

data class SubmissionRequestDTO(
    val studentIndex: String,
    val quizId: Long,
    val answers: Map<String, String>
)