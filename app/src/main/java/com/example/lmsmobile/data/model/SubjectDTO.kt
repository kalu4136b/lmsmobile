package com.example.lmsmobile.data.model

data class SubjectDto(
    val id: Long,
    val name: String,
    val code: String,
    val categoryName: String?,
    val categoryId: Long?,
    val degreeName: String?,
    val degreeId: Long?
)