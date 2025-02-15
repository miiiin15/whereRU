package com.miiiin15.whereru.domain.usecase

import com.miiiin15.whereru.domain.repository.LocationSessionRepository
import javax.inject.Inject

class GetRecentSessionListUseCase @Inject constructor(private val locationSessionRepository: LocationSessionRepository) {
    operator fun invoke(userId: String) =
        locationSessionRepository.getRecentSessionList(userId)
}