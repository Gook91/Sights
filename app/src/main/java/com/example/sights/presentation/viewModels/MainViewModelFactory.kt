package com.example.sights.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

// Фабрика вью-моделей
class MainViewModelFactory @Inject constructor(
    private val mainViewModel: MainViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return mainViewModel as T
        }
        throw java.lang.IllegalArgumentException("Unknown class name")
    }
}