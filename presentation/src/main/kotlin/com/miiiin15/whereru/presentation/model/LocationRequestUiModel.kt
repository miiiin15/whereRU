package com.miiiin15.whereru.presentation.model

import com.miiiin15.whereru.domain.model.LocationRequest
import com.miiiin15.whereru.domain.model.User

// UI에서 senderId, receiverId 대신 닉넴을 표시하도록 변환.
data class LocationRequestUiModel(
    val requestId: String,
    val senderNickname: String,
    val receiverNickname: String,
    val status: String
)

fun LocationRequest.toPresentation(sender: User, receiver: User) =
    LocationRequestUiModel(requestId, sender.nickname, receiver.nickname, status.name)