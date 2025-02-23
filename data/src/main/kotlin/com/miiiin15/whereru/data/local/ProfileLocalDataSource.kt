package com.miiiin15.whereru.data.local

import com.miiiin15.whereru.data.model.ProfileEntity

interface ProfileLocalDataSource {
    suspend fun getInitialProfiles(): List<ProfileEntity>
    suspend fun saveInitialProfiles(profiles: List<ProfileEntity>)
}