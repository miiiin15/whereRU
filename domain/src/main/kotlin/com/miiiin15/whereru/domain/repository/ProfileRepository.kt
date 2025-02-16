package com.miiiin15.whereru.domain.repository

import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.domain.model.User
import kotlinx.coroutines.flow.Flow

// 사용자 관련
interface ProfileRepository {
    fun getAllProfiles(): Flow<DataResource<List<User>>>
    fun getProfile(userId: String): Flow<DataResource<User>>
    fun setProfile(user: User): Flow<DataResource<Unit>>
    fun updateProfileSessionId(userId: String, sessionId:String): Flow<DataResource<Unit>>
    fun updateLastLogin(userId: String, lastLoginAt: Long): Flow<DataResource<Unit>>
    fun updateFcmToken(userId: String, fcmToken: String): Flow<DataResource<Unit>>
}
