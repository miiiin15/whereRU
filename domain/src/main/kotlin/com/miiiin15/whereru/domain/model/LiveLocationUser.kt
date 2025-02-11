package com.miiiin15.whereru.domain.model

data class LiveLocationUser(
    val userId: String,  // 사용자 ID
    val nickname: String, // 사용자 닉네임
    val liveLocationData: LiveLocationData // 현재 위치 정보
)
