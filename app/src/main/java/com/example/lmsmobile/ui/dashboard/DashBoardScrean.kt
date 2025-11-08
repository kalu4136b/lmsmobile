package com.example.lmsmobile.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lmsmobile.ui.dashboard.components.DashboardTopBar
import com.example.lmsmobile.ui.dashboard.components.SideBar
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun DashboardScreen(
    studentIndex: String,
    studentName: String,

) {
    val decodedName = URLDecoder.decode(studentName, StandardCharsets.UTF_8.name())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideBar(onItemClick = { label ->
                // TODO: Handle navigation based on label
            })
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            DashboardTopBar(
                drawerState = drawerState,
                scope = scope,
                onSearch = { query -> /* TODO */ },
                onNotificationClick = { /* TODO */ },
                onProfileClick = { /* TODO */ }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome, $decodedName!",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Index Number: $studentIndex",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "This is your LMS dashboard.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}