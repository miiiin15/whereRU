package com.miiiin15.whereru.remote.di

import com.miiiin15.whereru.data.remote.AuthRemoteDataSource
import com.miiiin15.whereru.remote.impl.AuthRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RemoteDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(source: AuthRemoteDataSourceImpl): AuthRemoteDataSource

}