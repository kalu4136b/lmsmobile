package com.example.lmsmobile.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.lmsmobile.data.model.QuestionDTO
import com.example.lmsmobile.data.model.QuizDTO
import com.example.lmsmobile.data.model.SubmissionResultDTO
import com.example.lmsmobile.ui.components.DashboardTopBar
import kotlinx.coroutines.launch

@Composable
fun ViewOnlineQuizScreen(
    indexNumber: String,
    viewModel: QuizViewModel,
    navController: NavHostController
) {
    val quiz by viewModel.quiz.collectAsState()
    val submitted by viewModel.submitted.collectAsState()
    val result by viewModel.result.collectAsState()
    val error by viewModel.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Holds answers before submission
    val answers = remember { mutableStateMapOf<Int, String>() }

    // Load quiz initially
    LaunchedEffect(indexNumber) {
        viewModel.loadQuiz(indexNumber)
    }

    // Show snackbar after submission result arrives
    LaunchedEffect(result) {
        result?.let {
            scope.launch {
                snackbarHostState.showSnackbar(
                    "🎉 Quiz submitted! Score: ${it.score} / ${it.total}"
                )
            }
        }
    }

    Scaffold(
        topBar = {
            DashboardTopBar(
                title = "Online Quiz",
                scope = scope,
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
                showNotificationIcon = false,
                showProfileIcon = false
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                error != null -> {
                    Text(
                        text = error ?: "Error loading quiz",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                submitted && result != null -> {
                    QuizResultDisplay(quiz = quiz, result = result)
                }

                submitted -> {
                    Text(
                        "✅ You have already submitted this quiz.",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                quiz != null -> {
                    Text(quiz!!.title, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text(quiz!!.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))

                    quiz!!.questions.forEachIndexed { index, question ->
                        QuestionCard(index, question, answers)
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (answers.size < quiz!!.questions.size) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "⚠️ Please answer all questions before submitting."
                                    )
                                }
                                return@Button
                            }

                            // ✅ Submit answers with quizId
                            viewModel.submitAnswers(indexNumber, quiz!!.id, answers)
                        },
                        enabled = !submitted,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(50.dp)
                    ) {
                        Text("Submit Quiz")
                    }
                }

                else -> {
                    Text("📭 No active quiz found.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun QuestionCard(
    index: Int,
    question: QuestionDTO,
    answers: MutableMap<Int, String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${index + 1}. ${question.question}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (question.options.isNotEmpty()) {
                question.options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = answers[index] == option,
                            onClick = { answers[index] = option }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(option, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                OutlinedTextField(
                    value = answers[index] ?: "",
                    onValueChange = { answers[index] = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Your answer") }
                )
            }
        }
    }
}

@Composable
fun QuizResultDisplay(quiz: QuizDTO?, result: SubmissionResultDTO?) {
    if (quiz == null || result == null) return

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "🎉 Quiz Submitted Successfully!",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Blue // ✅ success text in blue
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Score: ${result.score} / ${result.total}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(16.dp))
        quiz.questions.forEachIndexed { index, question ->
            val studentAnswer: String? = result.studentAnswers?.get(index)
            val isCorrect = studentAnswer == question.correctAnswer

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCorrect) Color(0xFFDFF6DD) else Color(0xFFFFE1E0)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("${index + 1}. ${question.question}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(4.dp))
                    Text("Your Answer: ${studentAnswer ?: "—"}", style = MaterialTheme.typography.bodyMedium)
                    Text("Correct Answer: ${question.correctAnswer ?: "—"}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}