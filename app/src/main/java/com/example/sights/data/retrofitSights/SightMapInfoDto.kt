package com.example.sights.data.retrofitSights

import com.example.sights.entity.SightMapInfo
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class SightMapInfoDto(
    @Json(name = "name") override val name: String,
    @Json(name = "osm") override val osm: String? = null,
    @Json(name = "xid") override val xid: String,
    @Json(name = "wikidata") override val wikidata: String? = null,
    @Json(name = "kinds") override val kinds: String,
    @Json(name = "point") override val point: SightMapPointDto
) : SightMapInfo
