package com.example.lmsmobile.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lmsmobile.data.model.QuizSummaryDTO
import com.example.lmsmobile.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizListViewModel : ViewModel() {
    private val _quizzes = MutableStateFlow<List<QuizSummaryDTO>>(emptyList())
    val quizzes: StateFlow<List<QuizSummaryDTO>> = _quizzes

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadQuizzes(studentIndex: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.apiService.listQuizzesForStudent(studentIndex)
                if (response.isSuccessful) {
                    _quizzes.value = response.body() ?: emptyList()
                } else {
                    _error.value = "No quizzes found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to load quizzes"
            } finally {
                _loading.value = false
            }
        }
    }
}