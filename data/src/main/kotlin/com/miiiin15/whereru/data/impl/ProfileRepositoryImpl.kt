package com.miiiin15.whereru.data.impl

import com.miiiin15.whereru.data.bound.flowDataResource
import com.miiiin15.whereru.data.model.ProfileEntity
import com.miiiin15.whereru.data.remote.ProfileRemoteDataSource
import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.domain.model.User
import com.miiiin15.whereru.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class ProfileRepositoryImpl @Inject constructor(
    private val profileRemoteDataSource: ProfileRemoteDataSource,
) : ProfileRepository {
    override fun setProfile(user: User): Flow<DataResource<Unit>> =
        flowDataResource {
            profileRemoteDataSource.setProfile(
                ProfileEntity(
                    user.userId,
                    user.nickname,
                    user.profileImageUrl,
                    user.lastLoginAt
                )
            )
        }

    override fun getProfile(userId: String): Flow<DataResource<User>> =
        flowDataResource {
            profileRemoteDataSource.getProfile(userId)
        }

    // TODO : local과 연계
}