package com.miiiin15.whereru.local.room.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface BaseDao<T> {

    // 단일 insert
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(vararg obj: T)

   // 리스트 insert
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(obj: List<T>)
}
