package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.domain.model.LiveLocationSession

// UI에서 실시간 위치 공유 세션 목록을 표시할 때 사용.
data class LocationSessionUiModel(
    val hostId: String,
    val isActive: Boolean,
    val startedAt: String
    // TODO : 유저 목록에 대한 ui 모델이 따로 필요한지 고려하기
)

// TODO : UI상에 어떤 시간을 표기 할건지 고려하기

fun LiveLocationSession.toPresentation() {
    val timeValue: String = startedAt.toString()
    LocationSessionUiModel(hostId, isActive, startedAt.toString())
}