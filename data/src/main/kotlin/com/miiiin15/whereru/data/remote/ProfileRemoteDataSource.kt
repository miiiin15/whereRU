package com.miiiin15.whereru.data.remote

import com.miiiin15.whereru.data.model.ProfileEntity

interface ProfileRemoteDataSource {
    suspend fun getAllProfiles(): List<ProfileEntity>
    suspend fun getProfile(userId: String): ProfileEntity
    suspend fun setProfile(user: ProfileEntity): Unit
    suspend fun updateProfileSessionId(userId: String, sessionId: String): Unit
    suspend fun updateLastLogin(userId: String, lastLoginAt: Long): Unit
    suspend fun updateFcmToken(userId: String, fcmToken: String): Unit
}