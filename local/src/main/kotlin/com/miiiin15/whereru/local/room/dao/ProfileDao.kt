package com.miiiin15.whereru.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.miiiin15.whereru.local.model.ProfileLocal
import com.miiiin15.whereru.local.room.RoomConstant

@Dao
interface ProfileDao : BaseDao<ProfileLocal> {

    // 유저 목록 조회
    @Query("SELECT * FROM ${RoomConstant.Table.USERS}")
    suspend fun getInitialProfiles(): List<ProfileLocal>

    // 유저 목록 초기화
    @Query("DELETE FROM ${RoomConstant.Table.USERS}")
    suspend fun clearProfiles()
}
