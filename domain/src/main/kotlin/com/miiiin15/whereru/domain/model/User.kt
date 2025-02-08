package com.miiiin15.whereru.domain.model

data class User(
    val userId: String,          // 사용자 고유 ID
    val nickname: String,        // 사용자 닉네임
    val profileImageUrl: String?, // 프로필 이미지 (선택)
    val lastLoginAt: Long // 마지막 접속 시간
)
