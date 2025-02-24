package com.miiiin15.whereru.local.di

import com.miiiin15.whereru.local.impl.PrefUtilImpl
import com.miiiin15.whereru.local.pref.PrefUtil
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PrefUtilModule {

    @Binds
    @Singleton
    internal abstract fun bindPrefUtil(
        impl: PrefUtilImpl
    ): PrefUtil
}