package com.example.lmsmobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DashboardTopBar(
    title: String = "LMS Dashboard",
    drawerState: DrawerState? = null,
    scope: CoroutineScope,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    showNotificationIcon: Boolean = true,
    showProfileIcon: Boolean = true,
    profileImageUrl: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF090979), Color(0xFF4B6CB7))
                )
            )
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Navigation Icon
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else if (drawerState != null) {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title
            if (title.isNotBlank()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        color = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Notification Icon
            if (showNotificationIcon) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // Profile Picture or Default Icon
            if (showProfileIcon) {
                IconButton(onClick = onProfileClick) {
                    if (!profileImageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = "http://192.168.115.93:8080$profileImageUrl?t=${System.currentTimeMillis()}",
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }
        }
    }
}
