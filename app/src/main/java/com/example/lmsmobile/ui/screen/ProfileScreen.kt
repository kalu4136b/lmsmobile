package com.example.lmsmobile.ui.screen

import com.example.lmsmobile.ui.components.DashboardTopBar
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

enum class PendingAction { NONE, GALLERY, CAMERA }

@Composable
fun ProfileScreen(indexNumber: String, viewModel: ProfileViewModel, navController: NavHostController) {
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val pendingAction = remember { mutableStateOf(PendingAction.NONE) }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            try {
                val file = File(context.cacheDir, "camera_preview.jpg")
                if (file.exists()) file.delete()
                FileOutputStream(file).use { out ->
                    it.compress(Bitmap.CompressFormat.JPEG, 100, out)
                }
                val uri = Uri.fromFile(file)
                selectedImageUri = uri

                // Upload immediately after capture
                isUploading = true
                scope.launch {
                    viewModel.uploadBitmap(indexNumber, it, context, snackbarHostState)
                    viewModel.loadProfile(indexNumber)
                    delay(500)
                    isUploading = false
                    selectedImageUri = null
                }
            } catch (e: Exception) {
                scope.launch {
                    snackbarHostState.showSnackbar("❌ Camera image error: ${e.message}")
                }
            }
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = when (pendingAction.value) {
            PendingAction.GALLERY -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissions[android.Manifest.permission.READ_MEDIA_IMAGES] == true
                } else {
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] == true
                }
            }

            PendingAction.CAMERA -> permissions[android.Manifest.permission.CAMERA] == true
            else -> false
        }

        if (granted) {
            when (pendingAction.value) {
                PendingAction.GALLERY -> galleryLauncher.launch("image/*")
                PendingAction.CAMERA -> cameraLauncher.launch(null)
                else -> {}
            }
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("❌ Access denied")
            }
        }

        pendingAction.value = PendingAction.NONE
    }

    // Load profile initially
    LaunchedEffect(indexNumber) {
        viewModel.loadProfile(indexNumber)
    }

    Scaffold(
        topBar = {

            DashboardTopBar(
                title = "My Profile",
                scope = scope,
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
                drawerState = null,
                showNotificationIcon = false,
                showProfileIcon = false,

            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // PROFILE IMAGE
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                when {
                    selectedImageUri != null -> {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    profile?.profileImageUrl?.isNotBlank() == true -> {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data("http://192.168.144.93:8080${profile!!.profileImageUrl}?t=${System.currentTimeMillis()}")
                                .memoryCachePolicy(CachePolicy.DISABLED)
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray)
                        )
                    }
                }

                if (isUploading) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Camera + Gallery buttons under image
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = {
                    pendingAction.value = PendingAction.GALLERY
                    val galleryPermissions =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES)
                        } else {
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    permissionLauncher.launch(galleryPermissions)
                }) {
                    Text("Gallery")
                }

                Button(onClick = {
                    pendingAction.value = PendingAction.CAMERA
                    permissionLauncher.launch(arrayOf(android.Manifest.permission.CAMERA))
                }) {
                    Text("Camera")
                }
            }

            // UPLOAD BUTTON
            selectedImageUri?.let { uri ->
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        isUploading = true
                        scope.launch {
                            viewModel.uploadImage(indexNumber, uri, context, snackbarHostState)
                            viewModel.loadProfile(indexNumber)
                            delay(500)
                            isUploading = false
                            selectedImageUri = null
                        }
                    },
                    enabled = !isUploading
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Uploading...")
                    } else {
                        Text("Upload Profile Image")
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Profile details (same font size)
            if (profile != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "👤 ${profile!!.fullName}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Text(
                        text = "🆔 ${profile!!.indexNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = MaterialTheme.typography.titleLarge.fontSize * 1.1f),
                        color = Color.DarkGray
                    )
                    Text(
                        text = "🎓 ${profile!!.degreeName}",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = MaterialTheme.typography.titleLarge.fontSize * 1.1f),
                        color = Color.DarkGray
                    )
                }
            } else {
                Text("Loading profile...", style = MaterialTheme.typography.bodyMedium)
            }

        }
    }
}


