package com.example.sights.di

import android.app.Application
import androidx.room.Room
import com.example.sights.data.roomDB.SightDao
import com.example.sights.data.roomDB.SightDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DBModule(
    private val application: Application
) {

    // Получаем экземпляр интерфейса для доступа к базе данных
    @Singleton
    @Provides
    fun provideSightDao(): SightDao {
        return Room.databaseBuilder(
            application.applicationContext,
            SightDatabase::class.java,
            SightDatabase.DB_NAME
        ).build().sightDao()
    }
}