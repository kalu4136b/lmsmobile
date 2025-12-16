package com.example.lmsmobile.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
 main

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lmsmobile.ui.dashboard.DashboardScreen
import com.example.lmsmobile.ui.dashboard.TaskScheduleScreen
import com.example.lmsmobile.ui.login.LoginScreen
import com.example.lmsmobile.ui.notes.AddNoteScreen
import com.example.lmsmobile.ui.notes.NotesScreen
import com.example.lmsmobile.ui.notes.NotesViewModel
import com.example.lmsmobile.ui.results.ResultsScreen
import com.example.lmsmobile.ui.screen.ProfileScreen
import com.example.lmsmobile.ui.screen.ProfileViewModel
import com.example.lmsmobile.ui.quiz.ViewOnlineQuizScreen
import com.example.lmsmobile.ui.quiz.QuizViewModel
import com.example.lmsmobile.ui.quiz.QuizListScreen
import com.example.lmsmobile.ui.quiz.QuizListViewModel
import com.example.lmsmobile.ui.subject.SubjectDisplayScreen
import com.example.lmsmobile.ui.note.NoteScreen
import com.example.lmsmobile.ui.note.LmsViewModel
import com.example.lmsmobile.ui.note.LmsViewModelFactory
import com.example.lmsmobile.ui.note.FullscreenImageScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val lmsViewModel: LmsViewModel = viewModel(factory = LmsViewModelFactory())

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier
    ) {
        // Login
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { response ->
                    val safeIndex = response.indexNumber.ifBlank { "unknown" }
                    val safeName = response.fullName.ifBlank { "Student" }
                    val encodedName = URLEncoder.encode(safeName.trim(), StandardCharsets.UTF_8.name())
                    val degreeId = response.degree?.id ?: 0L
                    val dashboardRoute = Routes.dashboardRoute(safeIndex, encodedName, degreeId)
                    navController.navigate(dashboardRoute) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

 main
        // Dashboard
=======
        composable(Routes.NOTES) {
            val context = LocalContext.current
            val vm: NotesViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
            )
            NotesScreen(vm) { navController.navigate(Routes.ADD_NOTE) }
        }

        composable(Routes.ADD_NOTE) {
            val vm: NotesViewModel = viewModel()
            AddNoteScreen(vm) { navController.popBackStack() }
        }


        // 🏠 Dashboard screen

        composable(
            route = Routes.DASHBOARD,
            arguments = listOf(
                navArgument("studentIndex") { type = NavType.StringType },
                navArgument("studentName") { type = NavType.StringType },
                navArgument("degreeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val studentIndex = backStackEntry.arguments?.getString("studentIndex") ?: "unknown"
            val encodedName = backStackEntry.arguments?.getString("studentName") ?: "Student"
            val decodedName = URLDecoder.decode(encodedName, StandardCharsets.UTF_8.name())
            val degreeId = backStackEntry.arguments?.getLong("degreeId") ?: 0L

            DashboardScreen(
                studentIndex = studentIndex,
                studentName = decodedName,
                degreeId = degreeId,
                navController = navController
            )
        }

 main
        // Task Schedule

            DashboardScreen(
                navController = navController,    // ✅ pass navController
                studentIndex = studentIndex,
                studentName = decodedName
        // 📋 Task Schedule screen
 main
        composable(
            route = Routes.TASK_SCHEDULE,
            arguments = listOf(navArgument("degreeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val degreeId = backStackEntry.arguments?.getLong("degreeId") ?: 0L
            TaskScheduleScreen(degreeId = degreeId)
        }

        // Results
        composable(
            route = Routes.RESULTS,
            arguments = listOf(
                navArgument("studentIndex") { type = NavType.StringType },
                navArgument("studentName") { type = NavType.StringType },
                navArgument("degreeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val studentIndex = backStackEntry.arguments?.getString("studentIndex") ?: "unknown"
            val encodedName = backStackEntry.arguments?.getString("studentName") ?: "Student"
            val decodedName = URLDecoder.decode(encodedName, StandardCharsets.UTF_8.name())
            val degreeId = backStackEntry.arguments?.getLong("degreeId") ?: 0L

            ResultsScreen(
                indexNumber = studentIndex,
                studentName = decodedName,
                degreeId = degreeId,
                navController = navController
            )
        }

        // Profile
        composable(
            route = Routes.PROFILE,
            arguments = listOf(navArgument("indexNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val indexNumber = backStackEntry.arguments?.getString("indexNumber") ?: "unknown"
            val viewModel: ProfileViewModel = viewModel()
            ProfileScreen(
                indexNumber = indexNumber,
                viewModel = viewModel,
                navController = navController
            )
        }

        // Quiz (single active quiz)
        composable(
            route = Routes.QUIZ,
            arguments = listOf(navArgument("indexNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val indexNumber = backStackEntry.arguments?.getString("indexNumber") ?: "unknown"
            val viewModel: QuizViewModel = viewModel()
            ViewOnlineQuizScreen(
                indexNumber = indexNumber,
                viewModel = viewModel,
                navController = navController
            )
        }

        // Quiz List
        composable(
            route = Routes.QUIZ_LIST,
            arguments = listOf(navArgument("studentIndex") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentIndex = backStackEntry.arguments?.getString("studentIndex") ?: "unknown"
            val viewModel: QuizListViewModel = viewModel()
            QuizListScreen(
                navController = navController,
                viewModel = viewModel,
                studentIndex = studentIndex
            )
        }

        // Subjects
        composable(
            route = Routes.SUBJECTS,
            arguments = listOf(navArgument("studentIndex") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentIndex = backStackEntry.arguments?.getString("studentIndex") ?: "unknown"
            SubjectDisplayScreen(
                studentIndex = studentIndex,
                viewModel = lmsViewModel,
                navController = navController
            )
        }

        // Notes
        composable(
            route = Routes.NOTE,
            arguments = listOf(
                navArgument("subjectId") { type = NavType.LongType },
                navArgument("subjectName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
            val encodedName = backStackEntry.arguments?.getString("subjectName") ?: "Subject"
            val decodedName = URLDecoder.decode(encodedName, StandardCharsets.UTF_8.name())

            NoteScreen(
                subjectId = subjectId,
                subjectName = decodedName,
                viewModel = lmsViewModel,
                navController = navController
            )
        }

        // Fullscreen Image Viewer
        composable(
            route = "image_view/{imageUrl}",
            arguments = listOf(navArgument("imageUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
            FullscreenImageScreen(
                imageUrl = imageUrl,
                navController = navController
            )
        }
    }
}