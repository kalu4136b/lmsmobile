package com.example.lmsmobile.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.BoxWithConstraints

@Composable
fun SideBar(
    onItemClick: (String) -> Unit = {}
) {
    BoxWithConstraints {
        val topMargin = maxHeight * 0.15f

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(240.dp)
                .padding(top = topMargin)
                .background(Color.White)
        ) {
            Text(
                text = "Dashboard",
                color = Color(0xFF090979),
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(start = 8.dp, top = 40.dp, bottom = 12.dp)
                    .clickable { onItemClick("Dashboard") }
            )

            Text(
                text = "Subjects",
                color = Color(0xFF090979),
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(start = 8.dp, top = 12.dp, bottom = 12.dp)
                    .clickable { onItemClick("Subjects") }
            )

            Text(
                text = "Profile",
                color = Color(0xFF090979),
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(start = 8.dp, top = 12.dp, bottom = 12.dp)
                    .clickable { onItemClick("Profile") }
            )
        }
    }
}