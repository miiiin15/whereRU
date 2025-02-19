package com.miiiin15.whereru.data.impl

import com.miiiin15.whereru.data.bound.flowDataResource
import com.miiiin15.whereru.data.remote.LocationSessionRemoteDataSource
import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.domain.model.JoinedSession
import com.miiiin15.whereru.domain.model.LiveLocationSession
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

    override fun deleteSession(sessionId: String): Flow<DataResource<Unit>> =
        flowDataResource {
            locationRemoteDataSource.deleteSession(sessionId)
        }

    override fun observeSession(
        sessionId: String,
        onSessionUpdated: (LiveLocationSession) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        locationRemoteDataSource.observeSession(
            sessionId,
            onSessionUpdated = { entity ->
                onSessionUpdated(entity.toDomain())
            },
            onError
        )
    }

    override fun stopObserveSession(sessionId: String): Flow<DataResource<Unit>> =
        flowDataResource {
            locationRemoteDataSource.stopObserveSession(sessionId)
        }

    override fun getRecentSessionList(userId: String): Flow<DataResource<List<JoinedSession>>> =
        flowDataResource {
            locationRemoteDataSource.getRecentSessionList(userId)
        }

    override fun participationSession(
        userId: String,
        targetSessionId: String,
        hostNickname: String,
        participationTime: Long
    ): Flow<DataResource<Unit>> =

        flowDataResource {
            locationRemoteDataSource.participationSession(
                userId,
                targetSessionId,
                hostNickname,
                participationTime
            )
        }

    override fun exitSession(userId: String, targetSessionId: String): Flow<DataResource<Unit>> =
        flowDataResource {
            locationRemoteDataSource.exitSession(userId, targetSessionId)
        }

    // TODO : local과 연계
}