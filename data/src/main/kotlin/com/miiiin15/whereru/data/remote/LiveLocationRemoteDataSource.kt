package com.miiiin15.whereru.data.remote

import com.miiiin15.whereru.data.model.UserLocationEntity


interface LiveLocationRemoteDataSource {

    suspend fun updateMyLocation(
        sessionId: String,
        userId: String,
        nickname: String,
        location: UserLocationEntity
    ): Unit

}