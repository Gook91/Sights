package com.example.sights.data.roomDB

import androidx.room.Database
import androidx.room.RoomDatabase

// Класс базы данных
@Database(entities = [SightDto::class], version = SightDatabase.VERSION)
abstract class SightDatabase: RoomDatabase() {
    // Возвращаем интерфейс доступа к таблице достопримечательностей
    abstract fun sightDao() : SightDao

    companion object {
        // Основные параметры базы данных
        const val VERSION = 1
        const val DB_NAME = "db"
    }
}