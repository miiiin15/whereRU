package com.miiiin15.whereru.remote.impl

import com.miiiin15.whereru.data.model.UserLocationEntity
import com.miiiin15.whereru.data.remote.LiveLocationRemoteDataSource
import com.miiiin15.whereru.remote.service.FirebaseService
import javax.inject.Inject

class LiveLocationRemoteDataSourceImpl @Inject constructor(
    private val firebaseService: FirebaseService
) : LiveLocationRemoteDataSource {

    override suspend fun updateMyLocation(
        sessionId: String,
        userId: String,
        nickname: String,
        location: UserLocationEntity
    ): Unit =
        firebaseService.updateMyLocation(sessionId, userId, nickname, location)
}
