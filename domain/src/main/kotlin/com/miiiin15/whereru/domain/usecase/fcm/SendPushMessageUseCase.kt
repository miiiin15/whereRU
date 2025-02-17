package com.miiiin15.whereru.domain.usecase.fcm

import com.miiiin15.whereru.domain.model.PushMessage
import com.miiiin15.whereru.domain.repository.FCMRepository
import javax.inject.Inject

class SendPushMessageUseCase @Inject constructor(
    private val fcmRepository: FCMRepository
) {
    operator fun invoke(
        token: String,
        message: PushMessage,
    ) = fcmRepository.sendPushMessage(token, message)
}