package com.miiiin15.whereru.data.di

import com.miiiin15.whereru.data.impl.AuthRepositoryImpl
import com.miiiin15.whereru.data.impl.FCMRepositoryImpl
import com.miiiin15.whereru.data.impl.LiveLocationRepositoryImpl
import com.miiiin15.whereru.data.impl.LocationSessionRepositoryImpl
import com.miiiin15.whereru.data.impl.ProfileRepositoryImpl
import com.miiiin15.whereru.domain.repository.AuthRepository
import com.miiiin15.whereru.domain.repository.FCMRepository
import com.miiiin15.whereru.domain.repository.LiveLocationRepository
import com.miiiin15.whereru.domain.repository.LocationSessionRepository
import com.miiiin15.whereru.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(repo: AuthRepositoryImpl): AuthRepository


    @Binds
    @Singleton
    abstract fun bindLiveLocationRepository(repo: LiveLocationRepositoryImpl): LiveLocationRepository

    @Binds
    @Singleton
    abstract fun bindLocationSessionRepository(repo: LocationSessionRepositoryImpl): LocationSessionRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(repo: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindFCMRepository(repo: FCMRepositoryImpl): FCMRepository
}