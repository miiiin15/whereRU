package com.miiiin15.whereru.domain.model

data class LocationSession(
    val sessionId: String,   // 세션 고유 ID
    val hostId: String, // 세션을 만든 사용자 ID
    val isActive: Boolean // 세션이 활성화 중인지 여부
)
