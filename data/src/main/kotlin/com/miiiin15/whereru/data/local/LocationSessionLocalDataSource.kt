package com.miiiin15.whereru.data.local

import com.miiiin15.whereru.data.model.JoinedSessionEntity

interface LocationSessionLocalDataSource {
    suspend fun getInitialJoinedSessions(): List<JoinedSessionEntity>
    suspend fun saveInitialJoinedSessions(sessions: List<JoinedSessionEntity>)
}