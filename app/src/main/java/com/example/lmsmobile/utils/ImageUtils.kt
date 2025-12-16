package com.example.lmsmobile.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

object ImageUtils {
    fun uriToMultipart(uri: Uri, context: Context): MultipartBody.Part? {
        val file = File(uri.path ?: return null)
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData("image", file.name, requestFile)
    }
}