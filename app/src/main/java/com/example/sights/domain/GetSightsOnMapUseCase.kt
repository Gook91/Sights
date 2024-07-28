package com.example.sights.domain

import com.example.sights.data.Repository
import com.example.sights.entity.SightMapInfo
import javax.inject.Inject

// Получаем список достопримечательностей на карте по координатам
class GetSightsOnMapUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(
        lonMin: Double,
        lonMax: Double,
        latMin: Double,
        latMax: Double
    ): List<SightMapInfo> {
        return repository.getSightsOnMap(lonMin, lonMax, latMin, latMax)
    }
}