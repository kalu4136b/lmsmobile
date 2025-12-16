package com.example.lmsmobile.ui.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lmsmobile.data.model.QuizSummaryDTO
import com.example.lmsmobile.ui.components.DashboardTopBar
import com.example.lmsmobile.util.formatIsoDateTime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizListScreen(
    navController: NavController,
    viewModel: QuizListViewModel,
    studentIndex: String
) {
    val quizzes by viewModel.quizzes.collectAsState()
    val error by viewModel.error.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(studentIndex) {
        viewModel.loadQuizzes(studentIndex)
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            // Use your custom DashboardTopBar
            DashboardTopBar(
                title = "Quizzes",
                scope = scope,
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
                showNotificationIcon = false,
                showProfileIcon = false
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (loading) {
                CircularProgressIndicator()
            } else if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(quizzes) { quiz ->
                        QuizCard(quiz) {
                            // 🔹 Navigate to quiz detail screen
                            navController.navigate("quiz_screen/${studentIndex}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizCard(quiz: QuizSummaryDTO, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = quiz.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Start: ${formatIsoDateTime(quiz.startTime)}")
            Text("End: ${formatIsoDateTime(quiz.endTime)}")
            if (quiz.submitted) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("✅ Already Submitted", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}