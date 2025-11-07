package com.example.lmsmobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lmsmobile.ui.dashboard.DashboardScreen
import com.example.lmsmobile.ui.login.LoginScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { response ->
                    val safeIndex = response.indexNumber ?: "unknown"
                    val rawName = response.name

                    // Handle null or "NULL" names from backend
                    val safeName = if (rawName.isNullOrBlank() || rawName == "NULL") {
                        "Student"
                    } else {
                        rawName
                    }

                    val encodedName = URLEncoder.encode(safeName.trim(), StandardCharsets.UTF_8.name())
                    navController.navigate(Routes.dashboardRoute(safeIndex, safeName)) //  uses the function
                }
            )
        }

        composable(
            route = Routes.DASHBOARD,
            arguments = listOf(
                navArgument("studentIndex") { type = NavType.StringType },
                navArgument("studentName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val studentIndex = backStackEntry.arguments?.getString("studentIndex") ?: "unknown"
            val encodedName = backStackEntry.arguments?.getString("studentName") ?: "Student"
            val decodedName = URLDecoder.decode(encodedName, StandardCharsets.UTF_8.name())

            DashboardScreen(studentIndex = studentIndex, studentName = decodedName)
        }
    }
}