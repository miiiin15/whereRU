package com.miiiin15.whereru.remote.impl

import com.miiiin15.whereru.data.remote.FCMRemoteDataSource
import com.miiiin15.whereru.remote.service.FirebaseService
import javax.inject.Inject

class FCMRemoteDataSourceImpl  @Inject constructor(
    private val firebaseService: FirebaseService
) : FCMRemoteDataSource {

    override suspend fun getFCMToken(): String =
        firebaseService.getFCMToken()
}