package com.miiiin15.whereru.remote.di

import com.miiiin15.whereru.data.remote.AuthRemoteDataSource
import com.miiiin15.whereru.data.remote.FCMRemoteDataSource
import com.miiiin15.whereru.data.remote.LiveLocationRemoteDataSource
import com.miiiin15.whereru.data.remote.LocationSessionRemoteDataSource
import com.miiiin15.whereru.data.remote.ProfileRemoteDataSource
import com.miiiin15.whereru.remote.impl.AuthRemoteDataSourceImpl
import com.miiiin15.whereru.remote.impl.FCMRemoteDataSourceImpl
import com.miiiin15.whereru.remote.impl.LiveLocationRemoteDataSourceImpl
import com.miiiin15.whereru.remote.impl.LocationSessionRemoteDataSourceImpl
import com.miiiin15.whereru.remote.impl.ProfileRemoteDataSourceImpl
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

    @Binds
    @Singleton
    abstract fun bindLiveLocationRemoteDataSource(source: LiveLocationRemoteDataSourceImpl): LiveLocationRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindLocationSessionRemoteDataSource(source: LocationSessionRemoteDataSourceImpl): LocationSessionRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindProfileRemoteDataSource(source: ProfileRemoteDataSourceImpl): ProfileRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFCMRemoteDataSource(source: FCMRemoteDataSourceImpl): FCMRemoteDataSource
}