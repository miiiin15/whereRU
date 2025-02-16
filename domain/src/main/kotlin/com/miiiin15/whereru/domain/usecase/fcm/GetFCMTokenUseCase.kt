package com.miiiin15.whereru.domain.usecase.fcm

import com.miiiin15.whereru.domain.repository.FCMRepository
import javax.inject.Inject

class GetFCMTokenUseCase @Inject constructor(
    private val fcmRepository: FCMRepository
) {
    operator fun invoke() = fcmRepository.getFCMToken()
}