package com.miiiin15.whereru.presentation.fcm

import android.content.Intent
import com.miiiin15.whereru.domain.model.PushMessage
import com.miiiin15.whereru.domain.model.PushType
import com.miiiin15.whereru.domain.model.ResponseType
import com.miiiin15.whereru.presentation.model.PushMessageUiModel
import com.miiiin15.whereru.presentation.model.toPresentation

object FCMMessageMapper {

    fun fromIntent(intent: Intent): PushMessageUiModel? {
        val typeStr = intent.getStringExtra("fcmType") ?: return null
        val responseStr = intent.getStringExtra("fcmResponse")?.takeIf { it.isNotEmpty() } ?: null

        val type = runCatching { PushType.valueOf(typeStr) }.getOrNull() ?: return null
        val response = responseStr?.let { runCatching { ResponseType.valueOf(it) }.getOrNull() }

        return PushMessage(
            type = type,
            fromUserId = intent.getStringExtra("fcmFromUserId") ?: "",
            fromNickname = intent.getStringExtra("fcmFromNickname") ?: "",
            fromToken = intent.getStringExtra("fcmFromToken") ?: "",
            toUserId = intent.getStringExtra("fcmToUserId") ?: "",
            sessionId = intent.getStringExtra("fcmSessionId") ?: "",
            response = response,
            timestamp = intent.getLongExtra("fcmTimestamp", 0L)
        ).toPresentation()
    }

    fun toIntentExtras(push: PushMessage): Intent =
        Intent().apply {
            putExtra("fcmType", push.type.name)
            putExtra("fcmFromUserId", push.fromUserId)
            putExtra("fcmFromNickname", push.fromNickname)
            putExtra("fcmFromToken", push.fromToken)
            putExtra("fcmToUserId", push.toUserId)
            putExtra("fcmSessionId", push.sessionId)
            putExtra("fcmResponse", push.response?.name)
            putExtra("fcmTimestamp", push.timestamp)
        }
}