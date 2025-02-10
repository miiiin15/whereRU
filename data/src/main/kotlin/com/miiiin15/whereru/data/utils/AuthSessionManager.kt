package com.miiiin15.whereru.data.utils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthSessionManager @Inject constructor() {
    private var uid: String? = null
        private set

    fun login(uid: String) {
        this.uid = uid
    }

    fun logout() {
        uid = null
    }

    fun getUid(): String? = uid

    fun isLoggedIn(): Boolean = uid != null

}