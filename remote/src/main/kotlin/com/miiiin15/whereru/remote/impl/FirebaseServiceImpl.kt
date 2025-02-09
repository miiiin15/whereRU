package com.miiiin15.whereru.remote.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.miiiin15.whereru.data.model.ProfileEntity
import com.miiiin15.whereru.data.model.UserLocationEntity
import com.miiiin15.whereru.remote.model.CreateSessionRequest
import com.miiiin15.whereru.remote.model.UserLocationRequest
import com.miiiin15.whereru.remote.service.FirebaseService
import com.miiiin15.whereru.remote.utils.FirebasePaths
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseFirestore: FirebaseFirestore
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

    override suspend fun updateMyLocation(
        sessionId: String,
        uid: String,
        nickname: String,
        location: UserLocationEntity
    ): Unit {

        val userLocation = UserLocationRequest(
            nickname = nickname,
            location = location
        )

        firebaseDatabase.getReference("${FirebasePaths.LOCATION_SESSIONS}/$sessionId/users/$uid")
            .setValue(userLocation)
            .await()
    }

    override suspend fun createSession(sessionId: String, hostId: String): Unit {

        val createSessionRequest = CreateSessionRequest(
            hostId = hostId,
            startedAt = System.currentTimeMillis(),
            isActive = true,
            users = emptyMap()
        )

        firebaseDatabase.getReference("${FirebasePaths.LOCATION_SESSIONS}/$sessionId")
            .setValue(createSessionRequest)
            .await()
    }

    override suspend fun setProfile(profile: ProfileEntity): Unit {
        firebaseFirestore.collection(FirebasePaths.USER_PROFILE)
            .document(profile.userId)
            .set(profile)
            .await()
    }

    override suspend fun getProfile(userId: String): ProfileEntity {
        return runCatching {
            firebaseFirestore.collection(FirebasePaths.USER_PROFILE)
                .document(userId)
                .get()
                .await()
                .toObject(ProfileEntity::class.java)!!
        }.getOrElse { throw Exception("프로필 조회 실패: ${it.message} ") }
    }
}

