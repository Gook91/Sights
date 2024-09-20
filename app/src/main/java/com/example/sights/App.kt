package com.example.sights

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.sights.di.AppComponent
import com.example.sights.di.DBModule
import com.example.sights.di.DaggerAppComponent
import com.google.firebase.messaging.FirebaseMessaging

class App : Application() {

    // Компонент зависимостей
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .dBModule(DBModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        // Если версия андроида больше 26, то создаём канал с уведомлениями
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel()

        // Получаем токен приложения
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            Log.d(LOG_TAG, "Token is \n${it.result}")
        }
    }

    // Функция, создающая канал с уведомлениями
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = getString(R.string.features_channel_title)
        val descriptionText = getString(R.string.features_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val featuresChannel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(featuresChannel)
    }

    companion object {
        private const val LOG_TAG = "LoggedErrors"
        const val CHANNEL_ID = "features_channel"
    }
}