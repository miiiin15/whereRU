package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.domain.model.LocationSession

// UI에서 실시간 위치 공유 세션 목록을 표시할 때 사용.
data class LocationSessionUiModel(
    val sessionId: String,
    val participants: List<String>,
    val createdAt: String
)

fun LocationSession.toPresentation() =
    LocationSessionUiModel(sessionId, participants, formatTimestamp(createdAt))