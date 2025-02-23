package com.miiiin15.whereru.local.impl

import com.miiiin15.whereru.data.local.ProfileLocalDataSource
import com.miiiin15.whereru.data.model.ProfileEntity
import com.miiiin15.whereru.local.model.toLocal
import com.miiiin15.whereru.local.room.DtoConverter
import com.miiiin15.whereru.local.room.dao.ProfileDao
import com.miiiin15.whereru.local.toData
import javax.inject.Inject

class ProfileLocalDataSourceImpl @Inject constructor(
    private val profileDao: ProfileDao
) : ProfileLocalDataSource {

    override suspend fun getInitialProfiles(): List<ProfileEntity> {
        return profileDao.getInitialProfiles().toData()
    }

    override suspend fun saveInitialProfiles(profiles: List<ProfileEntity>) {
        profileDao.insert(profiles.map { it.toLocal() })
    }
}