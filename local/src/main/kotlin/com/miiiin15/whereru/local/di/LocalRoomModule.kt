package com.miiiin15.whereru.local.di

import android.content.Context
import androidx.room.Room
import com.miiiin15.whereru.local.room.AppDatabase
import com.miiiin15.whereru.local.room.RoomConstant
import com.miiiin15.whereru.local.room.dao.ProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LocalRoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            RoomConstant.ROOM_DB_NAME
        )
            .fallbackToDestructiveMigration() //  스키마 변경 시 기존 데이터를 삭제하고 새로 생성
            .build()

    @Provides
    @Singleton
    fun provideProfileDao(database: AppDatabase): ProfileDao = database.profileDao()

    @Provides
    @Singleton
    fun provideJoinedSessionDao(database: AppDatabase) = database.joinedSessionDao()
}
