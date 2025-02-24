package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.common.utils.DateUtil.toRelativeTime
import com.miiiin15.whereru.domain.model.LiveLocationUser

// UI에서 사용자 ID와 좌표값만 표시하도록 변환.
data class LiveLocationUserUiModel(
    val userId: String,
    val nickname: String,
    val profileImageUrl: String?,
    val location: LocationUiModel,
)

fun LiveLocationUser.toPresentation(): LiveLocationUserUiModel {
    val timeValue: String = liveLocationData.timestamp.toRelativeTime()
    return LiveLocationUserUiModel(
        userId,
        nickname,
        profileImageUrl,
        LocationUiModel(
            liveLocationData.latitude,
            liveLocationData.longitude,
            timeValue
        )
    )
}