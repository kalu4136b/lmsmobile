package com.example.lmsmobile.ui.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.lmsmobile.data.model.LoginViewModel
import com.example.lmsmobile.data.model.LoginResponse

@Composable
fun LoginScreen(
    onLoginSuccess: (LoginResponse) -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    val indexNumber by loginViewModel.indexNumber.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val isLoading by loginViewModel.isLoading.collectAsState()
    val errorMessage by loginViewModel.errorMessage.collectAsState()
    val loginSuccess by loginViewModel.loginSuccess.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginSuccess) {
        loginSuccess?.let { response ->
            Log.d("LoginScreen", "Received name: ${response.name}")
            onLoginSuccess(response)
            loginViewModel.clearLoginSuccess()
            snackbarHostState.showSnackbar("Login successful!")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp), // 🔼 Push content down slightly from top
            contentAlignment = Alignment.TopCenter // 🔼 Align content to top
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // ✅ Logo from URL
                Image(
                    painter = rememberAsyncImagePainter("https://www.harlow-college.ac.uk/images/harlow_college/study-options/course-areas/bright-futures/redesign/bright-futures-logo-large-cropped.png"),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 8.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "LMS Login",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp),
                    color = Color.Black
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = indexNumber,
                    onValueChange = { loginViewModel.onIndexNumberChange(it) },
                    label = { Text("Index Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { loginViewModel.onPasswordChange(it) },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { loginViewModel.login() },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Login")
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}