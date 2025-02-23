package com.miiiin15.whereru.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.miiiin15.whereru.data.model.ProfileEntity
import com.miiiin15.whereru.local.model.ProfileLocal
import com.miiiin15.whereru.local.room.dao.ProfileDao

@Database(
    entities = [ProfileLocal::class],
    version = RoomConstant.ROOM_VERSION
)

@TypeConverters(
    DtoConverter::class
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao

}
