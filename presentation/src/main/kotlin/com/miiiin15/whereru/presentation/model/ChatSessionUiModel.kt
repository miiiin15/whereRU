package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.domain.model.ChatSession

// UI에서 createdAt을 변환하여 사용자가 읽기 쉽게 표시.
data class ChatSessionUiModel(
    val sessionId: String,
    val participants: List<String>,
    val createdAt: String
)

fun ChatSession.toPresentation() =
    ChatSessionUiModel(sessionId, participants, formatTimestamp(createdAt))