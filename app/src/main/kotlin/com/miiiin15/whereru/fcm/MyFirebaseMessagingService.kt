package com.miiiin15.whereru.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.app.NotificationCompat
import com.miiiin15.whereru.R
import com.miiiin15.whereru.domain.PushMessageMapper
import com.miiiin15.whereru.domain.model.PushType
import com.miiiin15.whereru.domain.model.ResponseType
import com.miiiin15.whereru.ui.main.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // 새로운 토큰을 서버에 전송하는 로직
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data  // Map<String, String>

        val pushMessage = PushMessageMapper.mapToPushMessage(data) ?: return

        when (pushMessage.type) {
            PushType.REQUEST_LOCATION -> {
                showNotification("위치 요청", "${pushMessage.fromNickname}님이 위치를 요청했어요.")
                // TODO: 인텐트 처리
            }

            PushType.RESPONSE_LOCATION -> {
                val decision = when (pushMessage.response) {
                    ResponseType.ACCEPT -> "수락"
                    ResponseType.DECLINE -> "거절"
                    else -> "알 수 없음"
                }
                showNotification("요청 응답", "${pushMessage.fromNickname}님이 요청을 $decision 했어요.")
            }

            PushType.CANCEL_SESSION -> {
                showNotification("세션 종료", "위치 공유가 종료되었습니다.")
            }
        }
    }

    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.channel_id)
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.whereru_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

}

