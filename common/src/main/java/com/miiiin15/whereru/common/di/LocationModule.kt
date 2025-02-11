package com.miiiin15.whereru.common.di

import android.content.Context
import com.miiiin15.whereru.common.location.FusedLocationTracker
import com.miiiin15.whereru.common.location.LocationTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideLocationTracker(
        @ApplicationContext context: Context
    ): LocationTracker = FusedLocationTracker(context)
}