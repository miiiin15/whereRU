package com.miiiin15.whereru.domain.usecase.profile

import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.domain.model.User
import com.miiiin15.whereru.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPaginatedProfilesUseCase @Inject constructor(private val profileRepository: ProfileRepository) {
    operator fun invoke(
        lastVisible: Long?,
        pageSize: Int
    ): Flow<DataResource<List<User>>> {
        return profileRepository.getPaginatedProfiles(
            lastVisible = lastVisible,
            pageSize = pageSize
        )
    }
}