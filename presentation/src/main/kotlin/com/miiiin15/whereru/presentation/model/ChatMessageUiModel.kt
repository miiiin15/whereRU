package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.domain.model.ChatMessage
import com.miiiin15.whereru.domain.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// UI에서 senderId 대신 닉네임을 표시하고, timestamp를 읽기 편하게 변환.
data class ChatMessageUiModel(
    val messageId: String,
    val senderNickname: String,
    val content: String,
    val timestamp: String
)

fun ChatMessage.toPresentation(sender: User) =
    ChatMessageUiModel(messageId, sender.nickname, content, formatTimestamp(timestamp))

fun formatTimestamp(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("yyyy.MM.dd.HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}