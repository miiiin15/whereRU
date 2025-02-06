package com.miiiin15.whereru.common.di

import com.miiiin15.whereru.common.utils.AuthSessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SessionModule {

    @Provides
    @Singleton
    fun provideAuthSessionManager(): AuthSessionManager = AuthSessionManager()
}