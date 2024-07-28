package com.example.sights.data.roomDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sights.entity.Sight

// Сущность достопримечательности, преобразованная в таблицу
@Entity(tableName = "sight")
data class SightDto(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int? = null,
    @ColumnInfo(name = "uri_photo") override val uriPhoto: String,
    @ColumnInfo(name = "date_photo") override val datePhoto: Long
) : Sight