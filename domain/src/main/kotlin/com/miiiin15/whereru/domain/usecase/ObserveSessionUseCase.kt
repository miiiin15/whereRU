package com.miiiin15.whereru.domain.usecase

import com.miiiin15.whereru.domain.model.LiveLocationSession
import com.miiiin15.whereru.domain.repository.LocationSessionRepository
import javax.inject.Inject

class ObserveSessionUseCase @Inject constructor(private val locationSessionRepository: LocationSessionRepository) {
    operator fun invoke(
        sessionId: String,
        onSessionUpdated: (LiveLocationSession) -> Unit,
        onError: (Throwable) -> Unit = {}
    ) {
        locationSessionRepository.observeSession(
            sessionId, onSessionUpdated, onError
        )
    }
}