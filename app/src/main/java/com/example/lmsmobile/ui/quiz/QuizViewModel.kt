package com.example.lmsmobile.ui.quiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lmsmobile.data.model.QuizDTO
import com.example.lmsmobile.data.model.SubmissionRequestDTO
import com.example.lmsmobile.data.model.SubmissionResultDTO
import com.example.lmsmobile.data.model.SubmissionResponseWrapper
import com.example.lmsmobile.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {

    private val _quiz = MutableStateFlow<QuizDTO?>(null)
    val quiz: StateFlow<QuizDTO?> = _quiz

    private val _submitted = MutableStateFlow(false)
    val submitted: StateFlow<Boolean> = _submitted

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _result = MutableStateFlow<SubmissionResultDTO?>(null)
    val result: StateFlow<SubmissionResultDTO?> = _result

    /**
     * Loads the active quiz for a student if available.
     */
    fun loadQuiz(studentIndex: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.apiService.getActiveQuiz(studentIndex)
                Log.d("QuizViewModel", "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    _submitted.value = body?.submitted ?: false
                    val rawQuiz = body?.quiz

                    if (rawQuiz != null && rawQuiz.isActive()) {
                        _quiz.value = rawQuiz
                    } else {
                        _quiz.value = null
                        _error.value = "No active quiz found"
                    }
                } else {
                    _submitted.value = false
                    _quiz.value = null
                    _error.value = "No active quiz found"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _submitted.value = false
                _quiz.value = null
                _error.value = "Failed to load quiz"
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Submits the quiz answers for a student.
     */
    fun submitAnswers(index: String, quizId: Long, answers: Map<Int, String>) {
        if (_submitted.value) return

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                // Convert Int keys to String for backend request
                val payload = SubmissionRequestDTO(
                    studentIndex = index,
                    quizId = quizId,
                    answers = answers.mapKeys { it.key.toString() }
                )

                val response = RetrofitClient.apiService.submitQuiz(payload)
                if (response.isSuccessful) {
                    val wrapper: SubmissionResponseWrapper? = response.body()
                    val result = wrapper?.result
                    _result.value = result
                    _submitted.value = true

                    Log.d("QuizViewModel", "Message: ${wrapper?.message}")
                    Log.d("QuizViewModel", "Score: ${result?.score}/${result?.total}")
                } else {
                    _error.value = "Submission failed"
                    Log.e("QuizViewModel", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Error submitting quiz"
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Clears the previous submission result.
     */
    fun clearResult() {
        _result.value = null
        _submitted.value = false
        _error.value = null
    }
}