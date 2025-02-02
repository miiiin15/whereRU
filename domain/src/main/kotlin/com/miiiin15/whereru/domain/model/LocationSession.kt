package com.miiiin15.whereru.domain.model

data class LocationSession(
    val sessionId: String,   // 세션 고유 ID
    val participants: List<String>, // 세션 참여자 ID 목록
    val createdAt: Long // 세션 생성 시간
)
