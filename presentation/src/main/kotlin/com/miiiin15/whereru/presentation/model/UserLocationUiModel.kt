package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.domain.model.UserLocation

// UI에서 사용자 ID와 좌표값만 표시하도록 변환.
data class UserLocationUiModel(
    val userId: String,
    val nickname: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)

fun UserLocation.toPresentation() =
    UserLocationUiModel(
        userId,
        nickname,
        myLocationData.latitude,
        myLocationData.longitude,
        myLocationData.timestamp
    )