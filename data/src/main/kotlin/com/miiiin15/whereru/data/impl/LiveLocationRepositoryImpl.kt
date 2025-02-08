package com.miiiin15.whereru.data.impl

import com.miiiin15.whereru.data.bound.flowDataResource
import com.miiiin15.whereru.data.model.UserLocationEntity
import com.miiiin15.whereru.data.remote.LiveLocationRemoteDataSource
import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.domain.model.LocationData
import com.miiiin15.whereru.domain.repository.LiveLocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class LiveLocationRepositoryImpl @Inject constructor(
    private val liveLocationRemoteDataSource: LiveLocationRemoteDataSource,
) : LiveLocationRepository {

    override fun updateMyLocation(
        sessionId: String,
        userId: String,
        nickname: String,
        location: LocationData
    ): Flow<DataResource<Unit>> = flowDataResource {

        liveLocationRemoteDataSource.updateMyLocation(
            sessionId,
            userId,
            nickname,
            UserLocationEntity(
                latitude = location.latitude,
                longitude = location.longitude,
                timestamp = location.timestamp
            )
        )
    }

    // TODO : local과 연계
}