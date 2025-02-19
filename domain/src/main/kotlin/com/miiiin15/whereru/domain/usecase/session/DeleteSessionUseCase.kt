package com.miiiin15.whereru.domain.usecase.session

import com.miiiin15.whereru.domain.repository.LocationSessionRepository
import javax.inject.Inject

class DeleteSessionUseCase @Inject constructor(private val locationSessionRepository: LocationSessionRepository) {
    operator fun invoke(sessionId: String) =
        locationSessionRepository.deleteSession(sessionId)
}
