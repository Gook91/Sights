package com.example.sights.domain

import com.example.sights.data.Repository
import com.example.sights.entity.Sight
import javax.inject.Inject

// Юз-кейс добавления достопримечательности
class AddSightUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(sight: Sight) {
        repository.addSight(sight)
    }
}