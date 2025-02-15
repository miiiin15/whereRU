package com.miiiin15.whereru.data.remote

import com.miiiin15.whereru.data.model.LiveLocationEntity


interface LocationSessionRemoteDataSource {

    suspend fun createSession(sessionId: String, hostId: String): Unit

    fun observeSession(
        sessionId: String,
        onSessionUpdated: (LiveLocationEntity) -> Unit,
        onError: (Throwable) -> Unit
    ): Unit

    suspend fun stopObserveSession(sessionId: String): Unit

    suspend fun participationSession(
        userId: String,
        targetSessionId: String,
        hostNickname: String,
        participationTime: Long
    ): Unit

    suspend fun exitSession(userId: String, targetSessionId: String): Unit

}