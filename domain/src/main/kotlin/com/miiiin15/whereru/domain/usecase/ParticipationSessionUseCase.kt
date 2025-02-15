package com.miiiin15.whereru.domain.usecase

import javax.inject.Inject

class ParticipationSessionUseCase @Inject constructor(
    private val locationSessionRepository: com.miiiin15.whereru.domain.repository.LocationSessionRepository
) {
    operator fun invoke(
        userId: String,
        targetSessionId: String,
        hostNickname: String,
        participationTime: Long
    ) =
        locationSessionRepository.participationSession(
            userId,
            targetSessionId,
            hostNickname,
            participationTime
        )
}