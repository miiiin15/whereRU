package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.common.utils.DateUtil.toRelativeTime
import com.miiiin15.whereru.domain.model.JoinedSession

// UI에서 최근 위치 공유 세션 목록을 표시할 때 사용.
data class JoinedSessionUiModel(
    val sessionId: String,
    val hostNickname: String,
    val participationTime: String
)


fun JoinedSession.toPresentation(): JoinedSessionUiModel {
    return JoinedSessionUiModel(sessionId, hostNickname, participationTime.toRelativeTime())
}