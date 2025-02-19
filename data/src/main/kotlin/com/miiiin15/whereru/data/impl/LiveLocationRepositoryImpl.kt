package com.miiiin15.whereru.data.impl

import com.miiiin15.whereru.data.bound.flowDataResource
import com.miiiin15.whereru.data.model.MyLocationEntity
import com.miiiin15.whereru.data.remote.LiveLocationRemoteDataSource
import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.domain.model.MyLocationData
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
        location: MyLocationData
    ): Flow<DataResource<Unit>> = flowDataResource {

        liveLocationRemoteDataSource.updateMyLocation(
            sessionId,
            userId,
            nickname,
            MyLocationEntity(
                latitude = location.latitude,
                longitude = location.longitude,
                timestamp = location.timestamp
            )
        )
    }

    override fun deleteMyLocation(sessionId: String, userId: String): Flow<DataResource<Unit>> =
        flowDataResource {
            liveLocationRemoteDataSource.deleteMyLocation(sessionId, userId)
        }

    // TODO : local과 연계
}