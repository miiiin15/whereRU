package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.common.utils.DateUtil.toRelativeTime
import com.miiiin15.whereru.domain.model.PushMessage

data class PushMessageUiModel(
    val type: PushUiType,
    val fromUserId: String,
    val fromNickname: String,
    val fromToken: String,
    val toUserId: String,
    val sessionId: String,
    val response: ResponseUiType?,
    val timestamp: String,
    val notificationId: Int = 0
)

fun PushMessage.toPresentation(): PushMessageUiModel {
    return PushMessageUiModel(
        type = this.type.toPresentation(),
        fromUserId = this.fromUserId,
        fromNickname = this.fromNickname,
        fromToken = this.fromToken,
        toUserId = this.toUserId,
        sessionId = this.sessionId,
        response = this.response?.toPresentation(),
        timestamp = this.timestamp.toRelativeTime(),
        notificationId = this.timestamp.toInt()
    )
}