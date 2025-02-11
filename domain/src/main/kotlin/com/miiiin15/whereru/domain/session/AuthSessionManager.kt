package com.miiiin15.whereru.domain.session

interface AuthSessionManager {
    val uid: String?
    val targetSessionId: String?

    fun login(uid: String)
    fun setTargetSessionId(sessionId: String?)
    fun clear()
    fun isLoggedIn(): Boolean
    fun isEmptyTargetSessionId(): Boolean
}