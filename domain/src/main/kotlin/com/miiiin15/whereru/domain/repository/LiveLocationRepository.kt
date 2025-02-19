package com.miiiin15.whereru.domain.repository

import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.domain.model.MyLocationData
import kotlinx.coroutines.flow.Flow

// 위치 관련
interface LiveLocationRepository {
    fun updateMyLocation(
        sessionId: String,
        userId: String,
        nickname: String,
        location: MyLocationData
    ): Flow<DataResource<Unit>>

    fun deleteMyLocation(sessionId: String, userId: String): Flow<DataResource<Unit>>
}
