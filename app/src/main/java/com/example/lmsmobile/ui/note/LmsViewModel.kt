package com.example.lmsmobile.ui.note


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lmsmobile.data.model.NoteDTO
import com.example.lmsmobile.data.model.SubjectDto
import com.example.lmsmobile.data.repository.LmsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class LmsViewModel(
    private val repository: LmsRepository
) : ViewModel() {

    private val _subjects = MutableStateFlow<List<SubjectDto>>(emptyList())
    val subjects: StateFlow<List<SubjectDto>> = _subjects

    private val _notes = MutableStateFlow<List<NoteDTO>>(emptyList())
    val notes: StateFlow<List<NoteDTO>> = _notes

    /** Load subjects for a student */
    fun loadSubjects(studentIndex: String) {
        viewModelScope.launch {
            _subjects.value = repository.getSubjects(studentIndex)
        }
    }

    /** Load notes for a subject */
    fun loadNotes(subjectId: Long) {
        viewModelScope.launch {
            _notes.value = repository.getNotes(subjectId)
        }
    }

    /** Add a note (simple DTO save) */
    fun addNote(note: NoteDTO) {
        viewModelScope.launch {
            repository.saveNote(note)
            loadNotes(note.subjectId)
        }
    }

    /**
     * Upload a note with text, image, and optional PDF.
     * subjectIdLong passed separately to reload notes after upload.
     */
    fun uploadNote(
        text: RequestBody,
        subjectId: RequestBody,
        image: MultipartBody.Part?,
        pdf: MultipartBody.Part?,
        subjectIdLong: Long
    ) {
        viewModelScope.launch {
            repository.uploadNote(text, subjectId, image, pdf)
            loadNotes(subjectIdLong)
        }
    }

    /** Delete a note */
    fun deleteNote(noteId: Long, subjectId: Long) {
        viewModelScope.launch {
            repository.deleteNote(noteId)
            loadNotes(subjectId)
        }
    }

    /** Update a note’s text */
    fun updateNote(noteId: Long, newText: String, subjectId: Long) {
        viewModelScope.launch {
            val updated = NoteDTO(id = noteId, text = newText, subjectId = subjectId)
            repository.updateNote(noteId, updated)
            loadNotes(subjectId)
        }
    }
}