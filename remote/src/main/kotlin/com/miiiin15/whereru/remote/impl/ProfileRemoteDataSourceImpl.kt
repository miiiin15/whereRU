package com.miiiin15.whereru.remote.impl

import com.miiiin15.whereru.data.model.ProfileEntity
import com.miiiin15.whereru.data.remote.ProfileRemoteDataSource
import com.miiiin15.whereru.remote.service.FirebaseService
import javax.inject.Inject

class ProfileRemoteDataSourceImpl @Inject constructor(
    private val firebaseService: FirebaseService
) : ProfileRemoteDataSource {

    override suspend fun getAllProfiles(): List<ProfileEntity> =
        firebaseService.getAllProfiles()

    override suspend fun getPaginatedProfiles(
        lastVisible: Long?,
        pageSize: Int
    ): List<ProfileEntity> =
        firebaseService.getPaginatedProfiles(lastVisible, pageSize)

    override suspend fun getProfile(userId: String): ProfileEntity =
        firebaseService.getProfile(userId)

    override suspend fun setProfile(
        profile: ProfileEntity
    ) = firebaseService.setProfile(profile)

    override suspend fun updateProfileSessionId(userId: String, sessionId: String) =
        firebaseService.updateProfileSessionId(userId, sessionId)

    override suspend fun updateLastLogin(userId: String, lastLoginAt: Long) =
        firebaseService.updateLastLogin(userId, lastLoginAt)

    override suspend fun updateFcmToken(userId: String, fcmToken: String) =
        firebaseService.updateFcmToken(userId, fcmToken)
}
