package com.miiiin15.whereru.domain.session

interface AuthSessionManager {
    val uid: String?
    val sessionId: String?

    fun login(uid: String)
    fun setSessionId(sessionId: String?)
    fun clear()
    fun isLoggedIn(): Boolean
    fun isEmptySessionId(): Boolean
}