package com.miiiin15.whereru.domain.repository

import com.miiiin15.whereru.data_resource.DataResource
import kotlinx.coroutines.flow.Flow

interface LocationSessionRepository {
    fun createSession(sessionId: String, hostId: String): Flow<DataResource<Unit>>
//  TODO : fun endSession(sessionId: String): Flow<DataResource<Unit>>
}
