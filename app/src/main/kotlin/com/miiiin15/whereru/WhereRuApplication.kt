package com.miiiin15.whereru

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.CallSuper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WhereRuApplication : Application() {

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        instance = this

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.channel_id), // 채널 ID
                getString(R.string.app_name), // 채널 이름
                NotificationManager.IMPORTANCE_HIGH  // 중요도
            ).apply {
                description = getString(R.string.channel_description)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        lateinit var instance: WhereRuApplication
            private set
    }
}