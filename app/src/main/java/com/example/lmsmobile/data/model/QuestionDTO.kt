package com.example.lmsmobile.data.model

data class QuestionDTO(

    val question: String,
    val type: String,
    val options: List<String> = emptyList(),
    val correctAnswer: String? = null
)
