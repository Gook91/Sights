package com.example.sights.di

import com.example.sights.presentation.viewModels.MainViewModelFactory
import com.example.sights.presentation.viewModels.MapViewModelFactory
import dagger.Component
import javax.inject.Singleton

// Компонент, возвращающий фабрику вью-моделей
@Singleton
@Component(modules = [DBModule::class, RetrofitModule::class])
interface AppComponent {
    fun mainViewModelFactory(): MainViewModelFactory
    fun mapViewModelFactory(): MapViewModelFactory
}