package com.miiiin15.whereru.data.remote

import com.miiiin15.whereru.data.model.ProfileEntity

interface ProfileRemoteDataSource {

    suspend fun setProfile(user: ProfileEntity): Unit

    suspend fun getProfile(userId: String): ProfileEntity

    suspend fun updateLastLogin(userId: String, lastLoginAt: Long): Unit

}