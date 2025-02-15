package com.miiiin15.whereru.domain.usecase.profile

import com.miiiin15.whereru.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateLastLoginUseCase @Inject constructor(private val profileRepository: ProfileRepository) {
    operator fun invoke(
        userId: String,
        lastLoginAt: Long
    ) =
        profileRepository.updateLastLogin(userId, lastLoginAt)
}