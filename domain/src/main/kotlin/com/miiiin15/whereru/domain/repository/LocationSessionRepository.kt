package com.miiiin15.whereru.domain.repository

import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.domain.model.JoinedSession
import com.miiiin15.whereru.domain.model.LiveLocationSession
import kotlinx.coroutines.flow.Flow

interface LocationSessionRepository {
    fun createSession(sessionId: String, hostId: String): Flow<DataResource<Unit>>

    fun deleteSession(sessionId: String): Flow<DataResource<Unit>>

    fun observeSession(
        sessionId: String,
        onSessionUpdated: (LiveLocationSession) -> Unit,
        onError: (Throwable) -> Unit = {}
    )

    fun stopObserveSession(sessionId: String): Flow<DataResource<Unit>>

    fun getRecentSessionList(userId: String): Flow<DataResource<List<JoinedSession>>>

    fun getPaginatedSessionList(
        userId: String,
        lastVisible: Long?,
        pageSize: Int
    ): Flow<DataResource<List<JoinedSession>>>

    fun participationSession(
        userId: String,
        targetSessionId: String,
        hostNickname:String,
        participationTime: Long
    ): Flow<DataResource<Unit>>

    fun exitSession(userId: String, targetSessionId: String): Flow<DataResource<Unit>>
}
