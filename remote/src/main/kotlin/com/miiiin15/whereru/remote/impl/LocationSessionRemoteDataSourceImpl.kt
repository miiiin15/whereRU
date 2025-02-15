package com.miiiin15.whereru.remote.impl

import com.miiiin15.whereru.data.model.LiveLocationEntity
import com.miiiin15.whereru.data.remote.LocationSessionRemoteDataSource
import com.miiiin15.whereru.remote.service.FirebaseService
import javax.inject.Inject

class LocationSessionRemoteDataSourceImpl @Inject constructor(
    private val firebaseService: FirebaseService
) : LocationSessionRemoteDataSource {
    override suspend fun createSession(sessionId: String, hostId: String): Unit =
        firebaseService.createSession(sessionId, hostId)

    override fun observeSession(
        sessionId: String,
        onSessionUpdated: (LiveLocationEntity) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        firebaseService.observeSession(
            sessionId,
            onSessionUpdated = { response ->
                onSessionUpdated(response.toData())
            },
            onError
        )
    }

    override suspend fun stopObserveSession(sessionId: String) {
        firebaseService.stopObserveSession(sessionId)
    }

    override suspend fun participationSession(
        userId: String,
        targetSessionId: String,
        hostNickname: String,
        participationTime: Long
    ) {
        firebaseService.participationSession(
            userId,
            targetSessionId,
            hostNickname,
            participationTime
        )
    }

    override suspend fun exitSession(userId: String, targetSessionId: String) {
        firebaseService.exitSession(userId, targetSessionId)
    }
}
