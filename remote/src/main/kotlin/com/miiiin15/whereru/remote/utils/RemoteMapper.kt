package com.miiiin15.whereru.remote.utils

interface RemoteMapper<DataModel> {
    fun toData(): DataModel
}
