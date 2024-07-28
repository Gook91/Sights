package com.example.sights.data.retrofitSights

import com.example.sights.entity.SightMapPoint
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SightMapPointDto(
    @Json(name = "lon") override val lon: Double,
    @Json(name = "lat") override val lat: Double
) : SightMapPoint