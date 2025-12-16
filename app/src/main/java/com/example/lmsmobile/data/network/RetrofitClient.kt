package com.example.lmsmobile.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
<<<<<<< Updated upstream
    private const val BASE_URL = "http://192.168.115.93:8080/api/"
=======
    private const val BASE_URL = "http://192.168.123.95:8080"
>>>>>>> Stashed changes


    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}