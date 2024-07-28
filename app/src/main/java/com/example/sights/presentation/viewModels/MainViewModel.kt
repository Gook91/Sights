package com.example.sights.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sights.domain.AddSightUseCase
import com.example.sights.domain.GetSightsUseCase
import com.example.sights.entity.Sight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val addSightUseCase: AddSightUseCase,
    getSightsUseCase: GetSightsUseCase
) : ViewModel() {
    // Поток со списком достопримечательностей
    val allSightsFlow = getSightsUseCase.execute().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    // Сохраняем новую достопримечательность
    fun addSight(sight: Sight) {
        viewModelScope.launch(Dispatchers.Default) {
            addSightUseCase.execute(sight)
        }
    }
}