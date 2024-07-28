package com.example.sights.domain

import com.example.sights.data.Repository
import com.example.sights.entity.Sight
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Юз-кейс получения достопримечательностей из базы данных
class GetSightsUseCase @Inject constructor(
    private val repository: Repository
) {
    fun execute(): Flow<List<Sight>> = repository.getSights()
}