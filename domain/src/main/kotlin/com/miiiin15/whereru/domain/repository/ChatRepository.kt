package com.miiiin15.whereru.domain.repository

import com.miiiin15.whereru.domain.model.ChatMessage
import com.miiiin15.whereru.domain.model.ChatSession
import kotlinx.coroutines.flow.Flow

// 채팅 관련
interface ChatRepository {
    fun createChatSession(session: ChatSession): Flow<Boolean>
    fun sendMessage(message: ChatMessage): Flow<Boolean>
    fun getMessages(sessionId: String): Flow<List<ChatMessage>>
}
