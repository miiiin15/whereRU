package com.miiiin15.whereru.domain.usecase

import com.miiiin15.whereru.domain.repository.LocationSessionRepository
import javax.inject.Inject

class ExitSessionUserCase @Inject constructor(private val locationSessionRepository: LocationSessionRepository) {
    operator fun invoke(userId: String, targetSessionId: String) =
        locationSessionRepository.exitSession(userId, targetSessionId)
}