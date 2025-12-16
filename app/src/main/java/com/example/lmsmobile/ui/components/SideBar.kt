package com.example.lmsmobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lmsmobile.navigation.Routes

@Composable
fun SideBar(
    navController: NavController,
    studentIndex: String
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp)
            .background(Color.White)
            .shadow(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            // Top gradient section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF090979), Color(0xFF4B6CB7))
                        )
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Menu",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            // Sidebar items
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                SidebarItem(label = "Results") {
                    navController.navigate(Routes.resultsRoute(studentIndex, "Student", 0L))
                }
                SidebarItem(label = "Quizzes") {
                    navController.navigate(Routes.quizListRoute(studentIndex))
                }
                SidebarItem(label = "Subjects") {
                    navController.navigate(Routes.subjectsRoute(studentIndex))
                }
                SidebarItem(label = "Logout") {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarItem(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .height(64.dp)
            .background(Color(0xFFe0e0ff), shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = label, color = Color(0xFF090979), fontSize = 18.sp)
    }
}