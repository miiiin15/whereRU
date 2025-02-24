package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.domain.model.LiveLocationSession

// UI에서 실시간 위치 공유 세션 목록을 표시할 때 사용.
data class LocationSessionUiModel(
    val hostId: String,
    val isActive: Boolean,
    val startedAt: String
)

fun LiveLocationSession.toPresentation(): LocationSessionUiModel {
    val timeValue: String = startedAt.toString()
    return LocationSessionUiModel(hostId, isActive, startedAt.toString())
}