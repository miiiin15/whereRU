package com.miiiin15.whereru.remote.service

import com.miiiin15.whereru.data.model.ProfileEntity
import com.miiiin15.whereru.data.model.MyLocationEntity
import com.miiiin15.whereru.remote.model.LiveLocationResponse


interface FirebaseService {

    suspend fun login(email: String, password: String): String

    suspend fun register(email: String, password: String): String

    suspend fun createSession(sessionId: String, hostId: String): Unit

    suspend fun updateMyLocation(
        sessionId: String,
        uid: String,
        nickname: String,
        location: MyLocationEntity
    ): Unit

    suspend fun setProfile(
        profile: ProfileEntity
    ): Unit

    suspend fun getProfile(userId: String): ProfileEntity

    suspend fun updateProfileSessionId(userId: String, sessionId: String): Unit

    suspend fun updateLastLogin(userId: String, lastLoginAt: Long): Unit

    fun observeSession(
        sessionId: String,
        onSessionUpdated: (LiveLocationResponse) -> Unit,
        onError: (Throwable) -> Unit = {}
    ): Unit

    suspend fun stopObserveSession(sessionId: String): Unit

}
