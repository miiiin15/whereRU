package com.miiiin15.whereru.data.remote

interface LocationSessionRemoteDataSource {
    suspend fun createSession(sessionId: String, hostId: String): Unit
}