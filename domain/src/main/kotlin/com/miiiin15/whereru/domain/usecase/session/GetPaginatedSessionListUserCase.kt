package com.miiiin15.whereru.domain.usecase.session

import com.miiiin15.whereru.domain.repository.LocationSessionRepository
import javax.inject.Inject

class GetPaginatedSessionListUserCase @Inject constructor(private val locationSessionRepository: LocationSessionRepository) {
    operator fun invoke(
        userId: String,
        lastVisible: Long?,
        pageSize: Int
    ) = locationSessionRepository.getPaginatedSessionList(userId, lastVisible, pageSize)
}