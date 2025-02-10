package com.miiiin15.whereru.data.utils

import javax.inject.Singleton

@Singleton
class AuthSessionManager {
    private var uid: String? = null
        private set

    private var sessionId: String? = null
        private set

    fun login(uid: String) {
        this.uid = uid
    }

    fun setSessionId(sessionId: String?) {
        this.sessionId = sessionId
    }

    fun logout() {
        uid = null
        sessionId = null
    }

    fun getUid(): String? = uid

    fun getSessionId(): String? = sessionId

    fun isLoggedIn(): Boolean = uid != null

    fun isEmptySessionId(): Boolean = sessionId.isNullOrBlank()

}