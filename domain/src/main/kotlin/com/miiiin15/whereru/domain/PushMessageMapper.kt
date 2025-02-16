package com.miiiin15.whereru.domain

import com.miiiin15.whereru.domain.model.PushMessage
import com.miiiin15.whereru.domain.model.PushType
import com.miiiin15.whereru.domain.model.ResponseType

/**
 * FCM 메시지의 data Map을 PushMessage 도메인 모델로 변환하는 매퍼
 */
object PushMessageMapper {

    /**
     * FCM 메시지의 data Map을 PushMessage 객체로 파싱
     * - 필수 항목이 누락되면 null 반환
     */
    fun mapToPushMessage(data: Map<String, String>): PushMessage? {
        val type = PushType.from(data["type"]) ?: return null
        val fromUserId = data["fromUserId"] ?: return null
        val toUserId = data["toUserId"] ?: return null
        val sessionId = data["sessionId"] ?: return null
        val fromNickname = data["fromNickname"] ?: ""
        val response = ResponseType.from(data["response"])
        val timestamp = data["timestamp"]?.toLongOrNull() ?: System.currentTimeMillis()

        return PushMessage(
            type = type,
            fromUserId = fromUserId,
            fromNickname = fromNickname,
            toUserId = toUserId,
            sessionId = sessionId,
            response = response,
            timestamp = timestamp
        )
    }
}