package com.miiiin15.whereru.domain.repository

import com.miiiin15.whereru.data_resource.DataResource
import kotlinx.coroutines.flow.Flow

interface FCMRepository {
    fun getFCMToken(): Flow<DataResource<String>>
}