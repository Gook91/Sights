package com.example.sights.data.retrofitSights

import com.example.sights.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

// API для загрузки информации о достопримечательностях
interface OpenTripMapApi {
    // Получаем достопримечательности по координатам
    @GET("/0.1/ru/places/bbox?lang=ru&format=json")
    suspend fun getSightsOnMap(
        @Query("lon_min") lonMin: Double,
        @Query("lon_max") lonMax: Double,
        @Query("lat_min") latMin: Double,
        @Query("lat_max") latMax: Double,
        @Query("limit") limitSights: Int = DEFAULT_LIMIT_SIGHTS_ON_MAP,
        @Query("apikey") apiKey: String = API_KEY
    ): List<SightMapInfoDto>

    companion object {
        // Количество загружаемых достопримечательностей по умолчанию, максимум 500
        private const val DEFAULT_LIMIT_SIGHTS_ON_MAP = 50

        // Ключ для отправки запроса
        private const val API_KEY = BuildConfig.API_KEY
    }
}