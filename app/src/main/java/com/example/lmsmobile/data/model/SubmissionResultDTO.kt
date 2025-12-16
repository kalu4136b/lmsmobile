package com.example.lmsmobile.data.model

data class SubmissionResponseWrapper(
    val message: String,
    val result: SubmissionResultDTO
)

data class SubmissionResultDTO(
    val score: Int,
    val total: Int,
    val studentAnswers: Map<Int, String>,
    val quiz: QuizDTO?
)