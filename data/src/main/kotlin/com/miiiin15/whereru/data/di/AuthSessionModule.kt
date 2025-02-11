package com.miiiin15.whereru.data.di

import com.miiiin15.whereru.data.utils.AuthSessionManagerImpl
import com.miiiin15.whereru.domain.session.AuthSessionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthSessionModule {


    @Binds
    @Singleton
    internal abstract fun bindSessionManager(
        impl: AuthSessionManagerImpl
    ): AuthSessionManager
}