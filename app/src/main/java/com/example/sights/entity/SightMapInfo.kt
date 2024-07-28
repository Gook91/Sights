package com.example.sights.entity

// Сущность информации о достопримечательности
interface SightMapInfo {
    val name: String
    val osm: String?
    val xid: String
    val wikidata: String?
    val kinds: String
    val point: SightMapPoint
}



