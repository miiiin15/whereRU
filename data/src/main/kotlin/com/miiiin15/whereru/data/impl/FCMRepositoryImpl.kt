package com.miiiin15.whereru.data.impl

import com.miiiin15.whereru.data.bound.flowDataResource
import com.miiiin15.whereru.data.remote.FCMRemoteDataSource
import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.domain.model.PushMessage
import com.miiiin15.whereru.domain.model.toMap
import com.miiiin15.whereru.domain.repository.FCMRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FCMRepositoryImpl @Inject constructor(
    private val fcmRemoteDataSource: FCMRemoteDataSource,
) : FCMRepository {
    override fun getFCMToken(): Flow<DataResource<String>> = flowDataResource {
        fcmRemoteDataSource.getFCMToken()
    }

    override fun sendPushMessage(
        token: String,
        message: PushMessage,
    ): Flow<DataResource<Boolean>> = flowDataResource {
        fcmRemoteDataSource.sendPushMessage(
            token = token,
            message = message.toMap(),
        )
    }
}