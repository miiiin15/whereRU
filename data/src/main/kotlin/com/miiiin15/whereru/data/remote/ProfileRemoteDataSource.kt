package com.miiiin15.whereru.data.remote

import com.miiiin15.whereru.data.model.ProfileEntity

interface ProfileRemoteDataSource {

    suspend fun setProfile(user: ProfileEntity): Unit

    suspend fun getProfile(userId: String): ProfileEntity

    suspend fun updateProfileSessionId(userId: String, sessionId: String): Unit

    suspend fun updateLastLogin(userId: String, lastLoginAt: Long): Unit

}