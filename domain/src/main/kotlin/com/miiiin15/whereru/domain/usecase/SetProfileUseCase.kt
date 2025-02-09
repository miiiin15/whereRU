package com.miiiin15.whereru.domain.usecase

import com.miiiin15.whereru.domain.model.User
import com.miiiin15.whereru.domain.repository.ProfileRepository
import javax.inject.Inject

class SetProfileUseCase @Inject constructor(private val profileRepository: ProfileRepository) {
    operator fun invoke(
        userId: String,
        nickname: String,
        profileImageUrl: String? = "",
        sessionId: String? = "",
        lastLoginAt: Long
    ) =
        profileRepository.setProfile(
            User(
                userId,
                nickname,
                profileImageUrl,
                sessionId,
                lastLoginAt
            )
        )
}