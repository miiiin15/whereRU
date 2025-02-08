package com.miiiin15.whereru.domain.model

data class UserLocation(
    val userId: String,  // 사용자 ID
    val nickname: String, // 사용자 닉네임
    val locationData: LocationData // 현재 위치 정보
)
