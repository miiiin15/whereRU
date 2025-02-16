package com.miiiin15.whereru.data.remote

interface FCMRemoteDataSource {
    suspend fun getFCMToken(): String
}