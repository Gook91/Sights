package com.example.sights.di

import com.example.sights.data.retrofitSights.OpenTripMapApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
class RetrofitModule {

    // Получаем экземпляр API для загрузки информации о достопримечательностях из интернета
    @Provides
    fun provideOpenTripMapApi(): OpenTripMapApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(OpenTripMapApi::class.java)
    }

    companion object {
        private const val BASE_URL = "https://api.opentripmap.com"
    }
}