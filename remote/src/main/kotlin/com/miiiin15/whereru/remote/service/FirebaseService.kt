package com.miiiin15.whereru.remote.service

import com.miiiin15.whereru.data.model.UserLocationEntity


interface FirebaseService {

    suspend fun login(email: String, password: String): String

    suspend fun register(email: String, password: String): String

    suspend fun createSession(sessionId: String, hostId: String): Unit

    suspend fun updateMyLocation(
        sessionId: String,
        uid: String,
        nickname: String,
        location: UserLocationEntity
    ): Unit

}