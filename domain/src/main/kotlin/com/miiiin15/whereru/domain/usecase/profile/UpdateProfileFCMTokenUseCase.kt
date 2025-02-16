package com.miiiin15.whereru.domain.usecase.profile

import com.miiiin15.whereru.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileFCMTokenUseCase @Inject constructor(private val profileRepository: ProfileRepository) {
    operator fun invoke(
        userId: String,
        fcmToken: String
    ) =
        profileRepository.updateFcmToken(userId, fcmToken)
}