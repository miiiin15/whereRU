package com.miiiin15.whereru.domain.repository

import com.miiiin15.whereru.domain.model.Location
import kotlinx.coroutines.flow.Flow

// 위치 관련
interface LocationRepository {
    fun getCurrentLocation(userId: String): Flow<Location>
    fun updateLocation(userId: String, location: Location): Flow<Boolean>
}
