package com.example.sights.data.roomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Интерфейс доступа к достопримечательностям в базе данных
@Dao
interface SightDao {

    // Получаем поток со всеми достопримечательностями
    @Query("SELECT * FROM sight")
    fun getAll(): Flow<List<SightDto>>

    // Добавляем достопримечательности в базу данных в архив
    @Insert
    suspend fun insert(sightDto: SightDto)
}