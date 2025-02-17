package com.miiiin15.whereru.data.remote

interface FCMRemoteDataSource {
    suspend fun getFCMToken(): String
    suspend fun sendPushMessage(
        token: String,
        message: Map<String, String>,
    ): Boolean
}