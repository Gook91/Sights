package com.example.sights.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

// Фабрика вью-моделей для фрагмента с картой
class MapViewModelFactory @Inject constructor(
    private val mapViewModel: MapViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(mapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return mapViewModel as T
        }
        throw java.lang.IllegalArgumentException("Unknown class name")
    }
}