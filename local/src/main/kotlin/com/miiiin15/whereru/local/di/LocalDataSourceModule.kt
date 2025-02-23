package com.miiiin15.whereru.local.di

import com.miiiin15.whereru.data.local.LocationSessionLocalDataSource
import com.miiiin15.whereru.data.local.ProfileLocalDataSource
import com.miiiin15.whereru.local.impl.LocationSessionLocalDataSourceImpl
import com.miiiin15.whereru.local.impl.ProfileLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LocalDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindProfileLocalDataResource(source: ProfileLocalDataSourceImpl): ProfileLocalDataSource

    @Binds
    @Singleton
    abstract fun bindLocationSessionLocalDataResource(source: LocationSessionLocalDataSourceImpl): LocationSessionLocalDataSource

}
