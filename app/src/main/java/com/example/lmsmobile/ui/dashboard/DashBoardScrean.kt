package com.example.lmsmobile.ui.dashboard

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
 main
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.lmsmobile.navigation.Routes
main
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.lmsmobile.data.network.RetrofitClient
import com.example.lmsmobile.data.repository.TaskRepository
import com.example.lmsmobile.navigation.Routes
import com.example.lmsmobile.ui.components.DashboardTopBar
import com.example.lmsmobile.ui.components.SideBar
import com.example.lmsmobile.ui.screen.ProfileViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun DashboardScreen(
    navController: NavHostController,
    studentIndex: String,
    studentName: String,
    degreeId: Long,
    navController: NavHostController
) {
    val decodedName = URLDecoder.decode(studentName, StandardCharsets.UTF_8.name())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        drawerState.close()
    }

    val taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(TaskRepository(RetrofitClient.apiService))
    )
    val tasks by taskViewModel.tasks.collectAsState()

    val profileViewModel: ProfileViewModel = viewModel()
    val profile by profileViewModel.profile.collectAsState()

    LaunchedEffect(degreeId) {
        Log.d("DashboardScreen", "Loading tasks for degreeId: $degreeId")
        taskViewModel.loadTasks(degreeId)
    }

    LaunchedEffect(studentIndex) {
        profileViewModel.loadProfile(studentIndex)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF090979), Color(0xFF4B6CB7))
                        )
                    )
            ) {
                Spacer(modifier = Modifier.height(90.dp))
                // 🔹 Updated SideBar usage
                SideBar(
                    navController = navController,
                    studentIndex = studentIndex
                )
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DashboardTopBar(
                title = "",
                drawerState = drawerState,
                scope = scope,
                showBackButton = false,
                onBackClick = {},
                onNotificationClick = {},
                onProfileClick = {
                    navController.navigate(Routes.profileRoute(studentIndex))
                },
                showNotificationIcon = true,
                showProfileIcon = true,
                profileImageUrl = profile?.profileImageUrl
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Welcome, ",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp)
                )
                Text(
                    text = decodedName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "\uD83D\uDC4B", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("📋 Task Schedule", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                if (tasks.isEmpty()) {
                    Text("No tasks available.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(tasks) { task ->
                            Log.d("DashboardScreen", "Rendering task: ${task.name}")
                            TaskCard(task)
                        }
                    }
                }
            }
        }
    }
}