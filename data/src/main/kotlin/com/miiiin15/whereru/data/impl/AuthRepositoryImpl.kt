package com.miiiin15.whereru.data.impl

import com.miiiin15.whereru.data.bound.flowDataResource
import com.miiiin15.whereru.data.remote.AuthRemoteDataSource
import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
) : AuthRepository {
    override fun login(email: String, password: String): Flow<DataResource<String>> =
        flowDataResource { authRemoteDataSource.login(email, password) }

    override fun register(
        email: String,
        password: String,
    ): Flow<DataResource<String>> =
        flowDataResource {
            authRemoteDataSource.register(email, password) }

    // TODO : local과 연계
}