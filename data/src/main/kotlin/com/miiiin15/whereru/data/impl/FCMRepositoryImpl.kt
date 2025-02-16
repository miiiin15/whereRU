package com.miiiin15.whereru.data.impl

import com.miiiin15.whereru.data.bound.flowDataResource
import com.miiiin15.whereru.data.remote.FCMRemoteDataSource
import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.domain.repository.FCMRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FCMRepositoryImpl @Inject constructor(
    private val fcmRemoteDataSource: FCMRemoteDataSource,
) : FCMRepository {
    override fun getFCMToken(): Flow<DataResource<String>> = flowDataResource {
        fcmRemoteDataSource.getFCMToken()
    }
}