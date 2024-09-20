package com.example.sights

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.sights.presentation.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// Сервис по обработке входящих сообщений из FCM
class NotificationService : FirebaseMessagingService() {
    // Обрабатываем полученное сообщение
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Если версия SDK андроида 33 или выше и разрешения не выданы, то выходим
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        // Получаем заголовок и текст сообщения
        val title = message.data[TITLE_MESSAGE]
        val text = message.data[TEXT_MESSAGE]

        // Получаем информацию о том, на каком экране открыть приложение
        val pendingIntent = when (message.data[DESTINATION]) {
            TO_TAKE_PHOTO -> NavDeepLinkBuilder(this)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.takePhotoFragment)
                .setComponentName(MainActivity::class.java)
                .createPendingIntent()
            TO_MAP -> NavDeepLinkBuilder(this)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.mapFragment)
                .setComponentName(MainActivity::class.java)
                .createPendingIntent()
            else -> NavDeepLinkBuilder(this)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.listFragment)
                .setComponentName(MainActivity::class.java)
                .createPendingIntent()
        }

        // Создаём уведомление с текстом и intent, который откроет нужный фрагмент
        val notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_app_icon)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Отправляем уведомление
        NotificationManagerCompat.from(this).notify(ID_NOTIFICATION, notification)
    }


    // Выводим в лог новый токен
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(LOG_TAG, "New token is \n$token")
    }

    companion object {
        private const val LOG_TAG = "LoggedErrors"
        private const val ID_NOTIFICATION = 2

        private const val TITLE_MESSAGE = "title"
        private const val TEXT_MESSAGE = "message"
        private const val DESTINATION = "destination"
        private const val TO_TAKE_PHOTO = "take_photo"
        private const val TO_MAP = "map"
    }
}
/* Формат сообщения
{
    "message":{
        "token":"...",
        "data":{
            "title":"Оглянись!",
            "message":"Каждый момент уже достопримечательность: сделай фото прямо сейчас, чтобы запомнить!",
            "destination":"take_photo"
        }
    }
}
*/