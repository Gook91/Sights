package com.example.sights

import android.app.Application
import com.example.sights.di.AppComponent
import com.example.sights.di.DBModule
import com.example.sights.di.DaggerAppComponent

class App: Application() {

    // Компонент зависимостей
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .dBModule(DBModule(this))
            .build()
    }
}