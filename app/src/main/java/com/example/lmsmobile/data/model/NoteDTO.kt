package com.example.lmsmobile.data.model

data class NoteDTO(
    val id: Long? = null,
    val text: String,
    val imageUri: String? = null,
    val pdfUri: String? = null,
    val timestamp: Long? = null,
    val subjectId: Long
)