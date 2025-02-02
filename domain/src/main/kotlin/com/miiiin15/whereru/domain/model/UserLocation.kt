package com.miiiin15.whereru.domain.model

data class UserLocation(
    val userId: String,  // 사용자 ID
    val location: Location // 현재 위치 정보
)
