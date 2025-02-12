package com.miiiin15.whereru.remote.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.miiiin15.whereru.data.model.LiveLocationEntity
import com.miiiin15.whereru.data.model.ProfileEntity
import com.miiiin15.whereru.data.model.MyLocationEntity
import com.miiiin15.whereru.remote.model.CreateSessionRequest
import com.miiiin15.whereru.remote.model.LiveLocationDataBlock
import com.miiiin15.whereru.remote.model.LiveLocationResponse
import com.miiiin15.whereru.remote.model.LiveLocationUserBlock
import com.miiiin15.whereru.remote.model.MyLocationRequest
import com.miiiin15.whereru.remote.service.FirebaseService
import com.miiiin15.whereru.remote.utils.FirebasePaths
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseFirestore: FirebaseFirestore
) : FirebaseService {

    // 세션 리스너를 관리하는 맵
    private val sessionListeners = mutableMapOf<String, ValueEventListener>()

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
        location: MyLocationEntity
    ): Unit {

        val myLocation = MyLocationRequest(
            nickname = nickname,
            location = location
        )

        firebaseDatabase.getReference("${FirebasePaths.LOCATION_SESSIONS}/$sessionId/users/$uid")
            .setValue(myLocation)
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

    override suspend fun updateProfileSessionId(userId: String, sessionId: String) {
        runCatching {
            firebaseFirestore.collection(FirebasePaths.USER_PROFILE)
                .document(userId)
                .update("sessionId", sessionId)
                .await()
        }.getOrElse { throw Exception("세션 ID 갱신 실패 : ${it.message}") }
    }

    override suspend fun updateLastLogin(userId: String, lastLoginAt: Long) {
        firebaseFirestore.collection(FirebasePaths.USER_PROFILE)
            .document(userId)
            .update("lastLoginAt", lastLoginAt)
            .await()
    }

    override fun observeSession(
        sessionId: String,
        onSessionUpdated: (LiveLocationResponse) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        println("👀Start Observe Location Session👀")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (!snapshot.exists()) onError(Exception("세션 정보 파싱 실패"))

                // 중간 노드의 PK 역할을 하는 uid 까지 포함하기 위한 직렬화 작업
                val hostUid = snapshot.child("host_uid").getValue(String::class.java) ?: ""
                val isActive = snapshot.child("is_active").getValue(Boolean::class.java) ?: false
                val startedAt = snapshot.child("started_at").getValue(Long::class.java) ?: 0L

                val usersSnapshot = snapshot.child("users")
                val users = mutableMapOf<String, LiveLocationUserBlock>()

                for (userSnap in usersSnapshot.children) {
                    val userId = userSnap.key ?: continue

                    val nickname = userSnap.child("nickname").getValue(String::class.java) ?: ""
                    val latitude =
                        userSnap.child("location/latitude").getValue(Double::class.java) ?: 0.0
                    val longitude =
                        userSnap.child("location/longitude").getValue(Double::class.java) ?: 0.0
                    val timestamp =
                        userSnap.child("location/timestamp").getValue(Long::class.java) ?: 0L

                    val user = LiveLocationUserBlock(
                        userId = userId,
                        nickname = nickname,
                        location = LiveLocationDataBlock(
                            latitude = latitude,
                            longitude = longitude,
                            timestamp = timestamp
                        )
                    )

                    users[userId] = user
                }

                val session = LiveLocationResponse(
                    hostUid = hostUid,
                    isActive = isActive,
                    startedAt = startedAt,
                    users = users
                )

                onSessionUpdated(session)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        }
        firebaseDatabase.getReference("${FirebasePaths.LOCATION_SESSIONS}/$sessionId")
            .addValueEventListener(listener)
        sessionListeners[sessionId] = listener
    }

    override suspend fun stopObserveSession(sessionId: String) {
        sessionListeners[sessionId]?.let {
            firebaseDatabase.getReference("${FirebasePaths.LOCATION_SESSIONS}/$sessionId")
                .removeEventListener(it)
            sessionListeners.remove(sessionId)
            println("⛔️Stop Observe Location Session⛔️")
        }
    }
    
}

