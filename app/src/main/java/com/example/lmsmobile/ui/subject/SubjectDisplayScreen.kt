package com.example.lmsmobile.ui.subject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lmsmobile.data.model.SubjectDto
import com.example.lmsmobile.navigation.Routes
import com.example.lmsmobile.ui.components.DashboardTopBar
import com.example.lmsmobile.ui.note.LmsViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun SubjectDisplayScreen(
    navController: NavController,
    viewModel: LmsViewModel,
    studentIndex: String
) {
    val subjects by viewModel.subjects.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSubjects(studentIndex)
    }

    Column(modifier = Modifier.fillMaxSize()) {

        DashboardTopBar(
            title = "Subjects",
            showBackButton = true,
            onBackClick = { navController.popBackStack() },
            scope = rememberCoroutineScope(),
            drawerState = null,
            showNotificationIcon = false,
            showProfileIcon = false
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(subjects) { subject ->
                SubjectCard(subject) {
                    val encodedName = URLEncoder.encode(subject.name, StandardCharsets.UTF_8.name())
                    navController.navigate(Routes.noteRoute(subject.id, encodedName))
                }
            }
        }
    }
}

@Composable
fun SubjectCard(subject: SubjectDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(subject.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}
