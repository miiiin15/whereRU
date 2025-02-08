package com.miiiin15.whereru.domain.usecase

import com.miiiin15.whereru.domain.repository.LocationSessionRepository
import javax.inject.Inject

class CreateSessionUseCase @Inject constructor(private val locationSessionRepository: LocationSessionRepository) {
    operator fun invoke(sessionId: String, hostId: String) =
        locationSessionRepository.createSession(sessionId, hostId)
}
