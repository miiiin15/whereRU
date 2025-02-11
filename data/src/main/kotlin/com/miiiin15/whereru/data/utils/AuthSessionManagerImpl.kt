package com.miiiin15.whereru.data.utils

import com.miiiin15.whereru.domain.session.AuthSessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AuthSessionManagerImpl @Inject constructor() : AuthSessionManager {

    private var _uid: String? = null
    private var _sessionId: String? = null

    override val uid: String?
        get() = _uid

    override val sessionId: String?
        get() = _sessionId

    override fun login(uid: String) {
        this._uid = uid
    }

    override fun setSessionId(sessionId: String?) {
        this._sessionId = sessionId
    }

    override fun clear() {
        _uid = null
        _sessionId = null
    }

    override fun isLoggedIn(): Boolean = _uid != null

    override fun isEmptySessionId(): Boolean = _sessionId.isNullOrBlank()
}