package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.common.utils.DateUtil.toRelativeTime
import com.miiiin15.whereru.domain.model.User

// UI에서 사용자 정보를 표시할 때 필요한 데이터만
data class UserUiModel(
    val userId: String,
    val nickname: String,
    val profileImageUrl: String?,
    val sessionId: String?,
    val lastLoginAt: String
)

fun User.toPresentation(): UserUiModel {
    return UserUiModel(userId, nickname, profileImageUrl, sessionId, lastLoginAt.toRelativeTime())
}