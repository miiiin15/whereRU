package com.miiiin15.whereru.domain.model

data class LiveLocationSession(
    val hostId: String, // 세션을 만든 사용자 ID
    val isActive: Boolean, // 세션이 활성화 중인지 여부
    val startedAt: Long , // 시작 시간
    val users: Map<String, LiveLocationUser> // 세션 참여자(들) 정보
)
