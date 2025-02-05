package com.miiiin15.whereru.remote.di

import com.miiiin15.whereru.remote.impl.FirebaseServiceImpl
import com.miiiin15.whereru.remote.service.FirebaseService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseBindModule {

    @Binds
    @Singleton
    abstract fun bindFirebaseService(
        impl: FirebaseServiceImpl
    ): FirebaseService
}