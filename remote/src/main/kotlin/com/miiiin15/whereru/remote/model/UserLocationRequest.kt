package com.miiiin15.whereru.remote.model

import com.miiiin15.whereru.data.model.UserLocationEntity

data class UserLocationRequest(
    val nickname: String = "",
    val location: UserLocationEntity
)