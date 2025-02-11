package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.domain.model.LiveLocationUser

// UI에서 사용자 ID와 좌표값만 표시하도록 변환.
data class LiveLocationUserUiModel(
    val userId: String,
    val nickname: String,
    val location: LocationUiModel,
)
// TODO : UI상에 어떤 시간을 표기 할건지 고려하기

fun LiveLocationUser.toPresentation(): LiveLocationUserUiModel {
    val timeValue: String = liveLocationData.timestamp.toString()
    return LiveLocationUserUiModel(
        userId,
        nickname,
        LocationUiModel(
            liveLocationData.latitude,
            liveLocationData.longitude,
            timeValue
        )
    )
}