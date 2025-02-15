package com.miiiin15.whereru.domain.usecase.session

import com.miiiin15.whereru.domain.repository.LocationSessionRepository
import javax.inject.Inject

class StopObserveSessionUseCase @Inject constructor(private val sessionRepository: LocationSessionRepository) {
    operator fun invoke(sessionId: String) = sessionRepository.stopObserveSession(sessionId)
}