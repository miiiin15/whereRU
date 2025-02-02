package com.miiiin15.whereru.domain.repository

import com.miiiin15.whereru.domain.model.LocationSession
import kotlinx.coroutines.flow.Flow

// 위치 공유 세션 관련
interface LocationSessionRepository {
    fun createSession(session: LocationSession): Flow<Boolean>
    fun getSession(sessionId: String): Flow<LocationSession>
    fun endSession(sessionId: String): Flow<Boolean>
}
