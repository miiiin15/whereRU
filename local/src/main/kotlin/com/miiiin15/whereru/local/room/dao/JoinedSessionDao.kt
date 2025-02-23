package com.miiiin15.whereru.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.miiiin15.whereru.local.model.JoinedSessionLocal
import com.miiiin15.whereru.local.room.RoomConstant

@Dao
interface JoinedSessionDao : BaseDao<JoinedSessionLocal> {

    //  최근 입장 세션 목록 조회
    @Query("SELECT * FROM ${RoomConstant.Table.JOINED_SESSIONS}")
    suspend fun getInitialJoinedSessions(): List<JoinedSessionLocal>

    // 최근 입장 세션 목록 초기화
    @Query("DELETE FROM ${RoomConstant.Table.JOINED_SESSIONS}")
    suspend fun clearJoinedSessions()
}
