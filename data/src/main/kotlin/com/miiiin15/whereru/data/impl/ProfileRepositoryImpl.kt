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

    override fun getAllProfiles(): Flow<DataResource<List<User>>> =
        flowDataResource {
            profileRemoteDataSource.getAllProfiles()
        }

    override fun getProfile(userId: String): Flow<DataResource<User>> =
        flowDataResource {
            profileRemoteDataSource.getProfile(userId)
        }

    override fun setProfile(user: User): Flow<DataResource<Unit>> =
        flowDataResource {
            profileRemoteDataSource.setProfile(
                ProfileEntity(
                    user.userId,
                    user.nickname,
                    user.profileImageUrl,
                    user.sessionId,
                    user.lastLoginAt
                )
            )
        }

    override fun updateProfileSessionId(
        userId: String,
        sessionId: String
    ): Flow<DataResource<Unit>> =
        flowDataResource {
            profileRemoteDataSource.updateProfileSessionId(userId, sessionId)
        }

    override fun updateLastLogin(userId: String, lastLoginAt: Long): Flow<DataResource<Unit>> =
        flowDataResource {
            profileRemoteDataSource.updateLastLogin(userId, lastLoginAt)
        }

    // TODO : local과 연계
}