package com.miiiin15.whereru.domain.repository

import com.miiiin15.whereru.data_resource.DataResource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<DataResource<String>>
    fun register(email: String, password: String): Flow<DataResource<String>>
}