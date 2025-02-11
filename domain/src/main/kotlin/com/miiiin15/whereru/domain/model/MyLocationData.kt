package com.miiiin15.whereru.domain.model

data class MyLocationData(
    val latitude: Double,  // 위도
    val longitude: Double, // 경도
    val timestamp: Long    // 위치 업데이트 시간
)
