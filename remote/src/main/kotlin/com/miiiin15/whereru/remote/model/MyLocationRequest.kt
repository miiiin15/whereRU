package com.miiiin15.whereru.remote.model

import com.miiiin15.whereru.data.model.MyLocationEntity

data class MyLocationRequest(
    val nickname: String = "",
    val profileImageUrl: String? = "",
    val location: MyLocationEntity
)