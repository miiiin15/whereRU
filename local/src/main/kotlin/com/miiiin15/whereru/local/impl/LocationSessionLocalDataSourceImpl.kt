package com.miiiin15.whereru.local.impl

import com.miiiin15.whereru.data.local.LocationSessionLocalDataSource
import com.miiiin15.whereru.data.model.JoinedSessionEntity
import com.miiiin15.whereru.local.model.toLocal
import com.miiiin15.whereru.local.room.dao.JoinedSessionDao
import com.miiiin15.whereru.local.toData
import javax.inject.Inject

class LocationSessionLocalDataSourceImpl @Inject constructor(
    private val joinedSessionDao: JoinedSessionDao
) : LocationSessionLocalDataSource {

    override suspend fun getInitialJoinedSessions(): List<JoinedSessionEntity> {
        return joinedSessionDao.getInitialJoinedSessions().toData()
    }

    override suspend fun saveInitialJoinedSessions(sessions: List<JoinedSessionEntity>) {
        if (sessions.isEmpty()) {
            joinedSessionDao.clearJoinedSessions()
        } else {
            joinedSessionDao.insert(sessions.map { it.toLocal() })
        }
    }
}