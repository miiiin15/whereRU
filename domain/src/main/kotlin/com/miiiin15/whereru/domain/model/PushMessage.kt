package com.miiiin15.whereru.domain.model

data class PushMessage(
    val type: PushType, // FCM 메시지 타입
    val fromUserId: String, // 발신자 ID
    val fromNickname: String, // 발신자 닉네임
    val fromToken: String, // 발신자 FCM 토큰
    val toUserId: String, // 수신자 ID
    val sessionId: String, // 세션 ID
    val response: ResponseType? = null, // 응답 타입 (수락/거절)
    val timestamp: Long  // 메시지 수신 시간
)

fun PushMessage.toMap(): Map<String, String> = buildMap {
    put("type", type.name)
    put("fromUserId", fromUserId)
    put("fromNickname", fromNickname)
    put("fromToken", fromToken)
    put("toUserId", toUserId)
    put("sessionId", sessionId)
    response?.let { put("response", it.name) }
    put("timestamp", timestamp.toString())
}