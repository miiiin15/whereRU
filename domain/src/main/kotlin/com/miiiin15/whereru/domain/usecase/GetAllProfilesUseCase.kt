package com.miiiin15.whereru.domain.usecase

import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.domain.model.User
import com.miiiin15.whereru.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllProfilesUseCase @Inject constructor(private val profileRepository: ProfileRepository) {
    operator fun invoke(): Flow<DataResource<List<User>>> {
        return profileRepository.getAllProfiles()
    }
}