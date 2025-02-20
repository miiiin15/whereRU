package com.miiiin15.whereru.data.remote

import com.miiiin15.whereru.data.model.MyLocationEntity


interface LiveLocationRemoteDataSource {

    suspend fun updateMyLocation(
        sessionId: String,
        userId: String,
        nickname: String,
        profileImageUrl: String?,
        location: MyLocationEntity
    ): Unit

    suspend fun deleteMyLocation(
        sessionId: String,
        userId: String
    ): Unit

}