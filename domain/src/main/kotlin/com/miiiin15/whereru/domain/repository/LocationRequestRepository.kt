package com.miiiin15.whereru.domain.repository

import com.miiiin15.whereru.domain.model.LocationRequest
import com.miiiin15.whereru.domain.model.RequestStatus
import kotlinx.coroutines.flow.Flow

// 위치 요청 관련
interface LocationRequestRepository {
    fun sendLocationRequest(request: LocationRequest): Flow<Boolean>
    fun getLocationRequests(userId: String): Flow<List<LocationRequest>>
    fun updateRequestStatus(requestId: String, status: RequestStatus): Flow<Boolean>
}
