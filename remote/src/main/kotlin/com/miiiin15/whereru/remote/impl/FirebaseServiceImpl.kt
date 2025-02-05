package com.miiiin15.whereru.remote.impl

import com.google.firebase.auth.FirebaseAuth
import com.miiiin15.whereru.remote.service.FirebaseService
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : FirebaseService {

    override suspend fun login(email: String, password: String): String {
        return runCatching {
            firebaseAuth.signInWithEmailAndPassword(email, password).await().user!!.uid
        }.getOrElse { throw Exception("로그인 실패: ${it.message}") }
    }

    override suspend fun register(email: String, password: String): String {
        return runCatching {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await().user!!.uid
        }.getOrElse { throw Exception("회원가입 실패: ${it.message}") }
    }
}

