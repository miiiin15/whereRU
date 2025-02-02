package com.miiiin15.whereru.domain.model

data class ChatSession(
    val sessionId: String,   // 채팅 세션 ID (위치 공유 세션과 동일)
    val participants: List<String>, // 채팅 참여자 ID 목록
    val createdAt: Long // 세션 생성 시간
)
