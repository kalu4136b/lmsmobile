package com.example.lmsmobile.ui.note


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lmsmobile.data.repository.LmsRepository
import com.example.lmsmobile.data.network.RetrofitClient

class LmsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LmsViewModel::class.java)) {
            return LmsViewModel(LmsRepository(RetrofitClient.apiService)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}