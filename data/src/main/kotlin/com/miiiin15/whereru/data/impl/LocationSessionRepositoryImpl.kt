package com.miiiin15.whereru.data.impl

import com.miiiin15.whereru.data.bound.flowDataResource
import com.miiiin15.whereru.data.remote.LiveLocationRemoteDataSource
import com.miiiin15.whereru.data.remote.LocationSessionRemoteDataSource
import com.miiiin15.whereru.data.toDomainModel
import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.domain.model.LocationData
import com.miiiin15.whereru.domain.repository.LiveLocationRepository
import com.miiiin15.whereru.domain.repository.LocationSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class LocationSessionRepositoryImpl @Inject constructor(
    private val locationRemoteDataSource: LocationSessionRemoteDataSource
) : LocationSessionRepository {

    override fun createSession(sessionId: String, hostId: String): Flow<DataResource<Unit>> =
        flowDataResource {
            locationRemoteDataSource.createSession(sessionId, hostId)
        }

    // TODO : local과 연계
}