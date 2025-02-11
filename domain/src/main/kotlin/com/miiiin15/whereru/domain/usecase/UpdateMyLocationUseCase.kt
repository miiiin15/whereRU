package com.miiiin15.whereru.domain.usecase

import com.miiiin15.whereru.domain.model.MyLocationData
import com.miiiin15.whereru.domain.repository.LiveLocationRepository
import javax.inject.Inject

class UpdateMyLocationUseCase @Inject constructor(private val liveLocationRepository: LiveLocationRepository) {
    operator fun invoke(
        sessionId: String,
        userId: String,
        nickname: String,
        location: MyLocationData
    ) =
        liveLocationRepository.updateMyLocation(sessionId, userId, nickname, location)
}