package com.example.sights.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sights.domain.GetSightsOnMapUseCase
import com.example.sights.entity.SightMapInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val getSightsOnMapUseCase: GetSightsOnMapUseCase
) : ViewModel() {
    // Поток возвращающий список достопримечательностей для карты
    private val _sightsOnMapFlow: MutableStateFlow<List<SightMapInfo>> =
        MutableStateFlow(emptyList())
    val sightsOnMapFlow = _sightsOnMapFlow.asStateFlow()

    // Обработчик исключений при выполнении запроса
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(LOG_TAG, "Error in getting sights by coordinates: $throwable", throwable)
        _isLoading.value = false
    }

    // Поток с состоянием загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading get() = _isLoading.asStateFlow()

    // Получаем список достопримечательностей для карты по координатам
    fun getSightByCoordinates(lonMin: Double, lonMax: Double, latMin: Double, latMax: Double) {
        viewModelScope.launch(exceptionHandler) {
            _isLoading.value = true
            val sights = getSightsOnMapUseCase.execute(lonMin, lonMax, latMin, latMax)
            _sightsOnMapFlow.value = sights
            _isLoading.value = false
        }
    }

    companion object {
        private const val LOG_TAG = "LoggedErrors"
    }
}