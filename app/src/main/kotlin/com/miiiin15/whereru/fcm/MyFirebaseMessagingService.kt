package com.miiiin15.whereru.fcm

import android.app.ActivityManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.app.NotificationCompat
import com.miiiin15.whereru.R
import com.miiiin15.whereru.domain.PushMessageMapper
import com.miiiin15.whereru.domain.model.PushMessage
import com.miiiin15.whereru.domain.model.PushType
import com.miiiin15.whereru.domain.model.ResponseType
import com.miiiin15.whereru.presentation.fcm.FCMMessageMapper
import com.miiiin15.whereru.presentation.fcm.FCMMessageHolder
import com.miiiin15.whereru.ui.main.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // TODO : 새로운 토큰을 서버에 전송하는 로직 필요시 작성
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data  // Map<String, String>
        val pushMessage = PushMessageMapper.mapToPushMessage(data) ?: return



        when (pushMessage.type) {
            PushType.REQUEST_LOCATION -> {
                if (isAppInForeground()) {
                    FCMMessageHolder.set(pushMessage)
                }
                showNotification("위치 요청", "${pushMessage.fromNickname}님이 위치를 요청했어요.", pushMessage)
            }

            PushType.RESPONSE_LOCATION -> {
                val decision = when (pushMessage.response) {
                    ResponseType.ACCEPT -> "수락"
                    ResponseType.DECLINE -> "거절"
                    else -> "알 수 없음"
                }
                if (isAppInForeground()) {
                    FCMMessageHolder.set(pushMessage)
                }
                showNotification(
                    "요청 응답",
                    "${pushMessage.fromNickname}님이 요청을 $decision 했어요.",
                    pushMessage
                )
            }

            PushType.CANCEL_SESSION -> {
                if (isAppInForeground()) {
                    FCMMessageHolder.set(pushMessage)
                }
                showNotification("세션 종료", "위치 공유가 종료되었습니다.", pushMessage)
            }
        }
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = packageName

        return appProcesses.any {
            it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    it.processName == packageName
        }
    }

    private fun showNotification(title: String, body: String, pushMessage: PushMessage) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtras(FCMMessageMapper.toIntentExtras(pushMessage).extras!!)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.channel_id)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.whereru_logo_red)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(pushMessage.timestamp.toInt(), notificationBuilder.build())
    }

}

