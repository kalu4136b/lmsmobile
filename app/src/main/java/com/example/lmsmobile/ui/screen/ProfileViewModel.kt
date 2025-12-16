package com.example.lmsmobile.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lmsmobile.data.model.ProfileResponse
import com.example.lmsmobile.data.model.UploadResponse
import com.example.lmsmobile.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel : ViewModel() {

    private val _profile = MutableStateFlow<ProfileResponse?>(null)
    val profile: StateFlow<ProfileResponse?> = _profile

    //Load user profile from backend
    fun loadProfile(indexNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getProfile(indexNumber)
                if (response.isSuccessful) {
                    _profile.value = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //Upload image from gallery
    fun uploadImage(
        indexNumber: String,
        uri: Uri,
        context: Context,
        snackbarHostState: SnackbarHostState
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return@launch withContext(Dispatchers.Main) {
                        snackbarHostState.showSnackbar("❌ Failed to read image")
                    }

                val file = File(context.cacheDir, "$indexNumber-profile.jpg")
                inputStream.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }

                uploadFile(indexNumber, file, snackbarHostState)
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    snackbarHostState.showSnackbar("❌ Upload error: ${e.message}")
                }
            }
        }
    }

    //Upload image from camera
    fun uploadBitmap(
        indexNumber: String,
        bitmap: Bitmap,
        context: Context,
        snackbarHostState: SnackbarHostState
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(context.cacheDir, "$indexNumber-camera.jpg")
                FileOutputStream(file).use { output ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
                }

                uploadFile(indexNumber, file, snackbarHostState)
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    snackbarHostState.showSnackbar("❌ Upload error: ${e.message}")
                }
            }
        }
    }

    //Shared upload logic for both camera and gallery
    private suspend fun uploadFile(
        indexNumber: String,
        file: File,
        snackbarHostState: SnackbarHostState
    ) {
        try {
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = RetrofitClient.apiService.uploadProfileImage(indexNumber, body)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val uploadResponse: UploadResponse? = response.body()
                    uploadResponse?.let {
                        snackbarHostState.showSnackbar("✅ ${it.message}")

                        // Update current profile instantly if image URL is returned
                        _profile.value = _profile.value?.copy(
                            profileImageUrl = it.profileImageUrl ?: _profile.value?.profileImageUrl
                        )


                        // Force reload from server to ensure backend sync
                        loadProfile(indexNumber)
                    }
                } else {
                    snackbarHostState.showSnackbar("❌ Upload failed: ${response.message()}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                snackbarHostState.showSnackbar("❌ Upload error: ${e.message}")
            }
        }
    }
}
