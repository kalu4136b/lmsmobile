package com.example.lmsmobile.data.repository

import com.example.lmsmobile.data.network.ApiService
import com.example.lmsmobile.data.model.ProfileResponse
import com.example.lmsmobile.data.model.UploadResponse

import okhttp3.MultipartBody
import retrofit2.Response

class ProfileRepository(private val api: ApiService) {
    suspend fun getProfile(indexNumber: String): Response<ProfileResponse> =
        api.getProfile(indexNumber)

    suspend fun uploadProfileImage(indexNumber: String, image: MultipartBody.Part): Response<UploadResponse> {
        return api.uploadProfileImage(indexNumber, image)
    }

}