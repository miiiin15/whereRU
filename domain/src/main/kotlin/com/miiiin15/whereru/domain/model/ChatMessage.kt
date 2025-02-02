package com.miiiin15.whereru.domain.model

data class ChatMessage(
    val messageId: String,   // 메시지 고유 ID
    val sessionId: String,   // 해당 메시지가 속한 세션 ID
    val senderId: String,    // 보낸 사람 ID
    val content: String,     // 메시지 내용
    val timestamp: Long,     // 전송 시간 (Unix Time)
    val messageType: MessageType // 메시지 유형
)

enum class MessageType {
    TEXT, IMAGE, SYSTEM
}
