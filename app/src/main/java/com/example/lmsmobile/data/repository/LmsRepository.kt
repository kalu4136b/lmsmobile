package com.example.lmsmobile.data.repository


import com.example.lmsmobile.data.model.NoteDTO
import com.example.lmsmobile.data.model.SubjectDto
import com.example.lmsmobile.data.network.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class LmsRepository(private val api: ApiService) {

    suspend fun getSubjects(index: String): List<SubjectDto> = api.getSubjects(index)

    suspend fun getNotes(subjectId: Long): List<NoteDTO> = api.getNotes(subjectId)

    suspend fun saveNote(note: NoteDTO): NoteDTO = api.saveNote(note)

    suspend fun deleteNote(noteId: Long) {
        api.deleteNote(noteId)
    }

    suspend fun updateNote(noteId: Long, note: NoteDTO): NoteDTO = api.updateNote(noteId, note)

    /**
     * Upload a note with text, image, and optional PDF.
     */
    suspend fun uploadNote(
        text: RequestBody,
        subjectId: RequestBody,
        image: MultipartBody.Part?,
        pdf: MultipartBody.Part?
    ) = api.uploadNote(text, subjectId, image, pdf)
}