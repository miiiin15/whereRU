package com.miiiin15.whereru.remote.impl

import com.miiiin15.whereru.data.remote.LocationSessionRemoteDataSource
import com.miiiin15.whereru.remote.service.FirebaseService
import javax.inject.Inject

class LocationSessionRemoteDataSourceImpl @Inject constructor(
    private val firebaseService: FirebaseService
) : LocationSessionRemoteDataSource {
    override suspend fun createSession(sessionId: String, hostId: String): Unit =
        firebaseService.createSession(sessionId, hostId)
}
