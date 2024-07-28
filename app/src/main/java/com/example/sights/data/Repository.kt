package com.example.sights.data

import com.example.sights.data.retrofitSights.OpenTripMapApi
import com.example.sights.data.roomDB.SightDao
import com.example.sights.data.roomDB.SightDto
import com.example.sights.entity.Sight
import com.example.sights.entity.SightMapInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor(
    private val sightDao: SightDao,
    private val openTripMapApi: OpenTripMapApi
) {
    // Получаем поток со всеми достопримечательностями из базы данных
    fun getSights(): Flow<List<Sight>> = sightDao.getAll()

    // Добавляем новую достопримечательность в базу данных
    suspend fun addSight(sight: Sight) {
        val transformedData = SightDto(
            null,
            sight.uriPhoto,
            sight.datePhoto
        )
        sightDao.insert(transformedData)
    }

    // Получаем список достопримечательностей на карте по координатам
    suspend fun getSightsOnMap(
        lonMin: Double,
        lonMax: Double,
        latMin: Double,
        latMax: Double
    ): List<SightMapInfo> {
        return openTripMapApi.getSightsOnMap(lonMin, lonMax, latMin, latMax)
    }
}