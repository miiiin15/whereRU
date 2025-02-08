package com.miiiin15.whereru.domain.repository

import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.domain.model.LocationData
import kotlinx.coroutines.flow.Flow

// 위치 공유 세션 관련
interface LocationSessionRepository {
    fun createSession(sessionId: String, hostId: String): Flow<DataResource<Unit>>
//  fun endSession(sessionId: String): Result<Unit>
//  fun observeSession(sessionId: String): Flow<Map<String, LocationData>>
}
