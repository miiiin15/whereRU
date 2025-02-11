package com.miiiin15.whereru.data.utils

import com.miiiin15.whereru.domain.session.AuthSessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AuthSessionManagerImpl @Inject constructor() : AuthSessionManager {

    private var _uid: String? = null
    private var _targetSessionId: String? = null

    override val uid: String?
        get() = _uid

    override val targetSessionId: String?
        get() = _targetSessionId

    override fun login(uid: String) {
        this._uid = uid
    }

    override fun setTargetSessionId(sessionId: String?) {
        this._targetSessionId = sessionId
    }

    override fun clear() {
        _uid = null
        _targetSessionId = null
    }

    override fun isLoggedIn(): Boolean = _uid != null

    override fun isEmptyTargetSessionId(): Boolean = _targetSessionId.isNullOrBlank()
}