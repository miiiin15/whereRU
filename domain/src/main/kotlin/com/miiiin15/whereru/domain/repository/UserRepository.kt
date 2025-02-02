package com.miiiin15.whereru.domain.repository

import com.miiiin15.whereru.domain.model.User
import kotlinx.coroutines.flow.Flow

// 사용자 관련
interface UserRepository {
    fun getUser(userId: String): Flow<User>
    fun updateUser(user: User): Flow<Boolean>
}
