package com.miiiin15.whereru.remote.impl

import android.provider.Settings.Global.getString
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.miiiin15.whereru.data.model.JoinedSessionEntity
import com.miiiin15.whereru.data.model.ProfileEntity
import com.miiiin15.whereru.data.model.MyLocationEntity
import com.miiiin15.whereru.remote.model.CreateSessionRequest
import com.miiiin15.whereru.remote.model.LiveLocationDataBlock
import com.miiiin15.whereru.remote.model.LiveLocationResponse
import com.miiiin15.whereru.remote.model.LiveLocationUserBlock
import com.miiiin15.whereru.remote.model.MyLocationRequest
import com.miiiin15.whereru.remote.model.ParticipationSessionRequest
import com.miiiin15.whereru.remote.service.FirebaseService
import com.miiiin15.whereru.remote.utils.FirebasePaths
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseMessaging: FirebaseMessaging
) : FirebaseService {

    // 세션 리스너를 관리하는 맵
    private val sessionListeners = mutableMapOf<String, ValueEventListener>()

    /**
     * 일반 이메일 로그인
     * **/
    override suspend fun login(email: String, password: String): String {
        return runCatching {
            firebaseAuth.signInWithEmailAndPassword(email, password).await().user!!.uid
        }.getOrElse { throw Exception("로그인 실패: ${it.message}") }
    }

    /**
     * 일반 이메일 회원가입
     * **/
    override suspend fun register(email: String, password: String): String {
        return runCatching {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await().user!!.uid
        }.getOrElse { throw Exception("회원가입 실패: ${it.message}") }
    }

    /**
     * 내 위치 업데이트
     * **/
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

    /**
     * 내 위치 삭제
     * **/
    override suspend fun deleteMyLocation(
        sessionId: String,
        uid: String
    ): Unit {
        firebaseDatabase.getReference("${FirebasePaths.LOCATION_SESSIONS}/$sessionId/users/$uid")
            .removeValue()
            .await()
    }

    /**
     * 세션 생성
     * **/
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

    /**
     * 세션 삭제
     * **/
    override suspend fun deleteSession(sessionId: String): Unit {
        firebaseDatabase.getReference("${FirebasePaths.LOCATION_SESSIONS}/$sessionId")
            .removeValue()
            .await()
    }

    /**
     * 최근 참여한 모든 세션 조회
     * **/
    override suspend fun getRecentSessionList(userId: String): List<JoinedSessionEntity> {
        return runCatching {
            firebaseFirestore.collection("${FirebasePaths.JOINED_SESSIONS}/$userId/sessions")
                .get()
                .await()
                .documents.map { document ->
                    document.toObject(JoinedSessionEntity::class.java)
                        ?.copy(sessionId = document.id)
                }.filterNotNull()
        }.getOrElse { throw Exception("세션 조회 실패: ${it.message}") }
    }

    /**
     * 세션 참가
     * **/
    override suspend fun participationSession(
        userId: String,
        targetSessionId: String,
        hostNickname: String,
        participationTime: Long
    ): Unit {
        runCatching {
            val participationRequest = ParticipationSessionRequest(hostNickname, participationTime)

            firebaseFirestore.collection("${FirebasePaths.JOINED_SESSIONS}/$userId/sessions")
                .document(targetSessionId)
                .set(participationRequest)
                .await()
        }.getOrElse {
            throw Exception("세션 기록 실패: ${it.message}")
        }
    }

    /**
     * 세션 이탈
     * **/
    override suspend fun exitSession(userId: String, targetSessionId: String): Unit {
        runCatching {
            firebaseFirestore.collection("${FirebasePaths.JOINED_SESSIONS}/$userId/sessions")
                .document(targetSessionId)
                .delete()
                .await()
        }.getOrElse {
            throw Exception("세션 이탈 실패: ${it.message}")
        }
    }


    /**
     * 모든 프로필 조회
     * **/
    override suspend fun getAllProfiles(): List<ProfileEntity> {
        return runCatching {
            firebaseFirestore.collection(FirebasePaths.USER_PROFILE)
                .get()
                .await()
                .toObjects(ProfileEntity::class.java)
        }.getOrElse { throw Exception("프로필 조회 실패: ${it.message}") }
    }

    /**
     * 단일 프로필 조회
     * **/
    override suspend fun getProfile(userId: String): ProfileEntity {
        return runCatching {
            firebaseFirestore.collection(FirebasePaths.USER_PROFILE)
                .document(userId)
                .get()
                .await()
                .toObject(ProfileEntity::class.java)!!
        }.getOrElse { throw Exception("프로필 조회 실패: ${it.message} ") }
    }

    /**
     * 프로필 저장
     * **/
    override suspend fun setProfile(profile: ProfileEntity): Unit {
        firebaseFirestore.collection(FirebasePaths.USER_PROFILE)
            .document(profile.userId)
            .set(profile)
            .await()
    }

    /**
     * 프로필 세션 ID 업데이트
     * **/
    override suspend fun updateProfileSessionId(userId: String, sessionId: String) {
        runCatching {
            firebaseFirestore.collection(FirebasePaths.USER_PROFILE)
                .document(userId)
                .update("sessionId", sessionId)
                .await()
        }.getOrElse { throw Exception("세션 ID 갱신 실패 : ${it.message}") }
    }

    /**
     * 마지막 로그인 시간 업데이트
     * **/
    override suspend fun updateLastLogin(userId: String, lastLoginAt: Long) {
        runCatching {
            firebaseFirestore.collection(FirebasePaths.USER_PROFILE)
                .document(userId)
                .update("lastLoginAt", lastLoginAt)
                .await()
        }.getOrElse {
            throw Exception("마지막 로그인 시간 갱신 실패 : ${it.message}")
        }
    }

    /**
     * FCM 토큰 업데이트
     * **/
    override suspend fun updateFcmToken(userId: String, fcmToken: String) {
        runCatching {
            firebaseFirestore.collection(FirebasePaths.USER_PROFILE)
                .document(userId)
                .update("fcmToken", fcmToken)
                .await()
        }.getOrElse { throw Exception("FCM 토큰 갱신 실패 : ${it.message}") }
    }

    /**
     * 위치 공유 세션 추적
     * **/
    override fun observeSession(
        sessionId: String,
        onSessionUpdated: (LiveLocationResponse) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        println("👀Start Observe Location Session👀")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (!snapshot.exists()) onError(Exception("세션 정보가 유효하지 않습니다."))

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

    /**
     * 세션 종료
     * **/
    override suspend fun stopObserveSession(sessionId: String) {
        sessionListeners[sessionId]?.let {
            firebaseDatabase.getReference("${FirebasePaths.LOCATION_SESSIONS}/$sessionId")
                .removeEventListener(it)
            sessionListeners.remove(sessionId)
            println("⛔️Stop Observe Location Session⛔️")
        }
    }

    /**
     * FCM 토큰 가져오기
     * **/
    override suspend fun getFCMToken(): String {
        return runCatching {
            firebaseMessaging.token.await()
        }.getOrElse { throw Exception("FCM 토큰 가져오기 실패: ${it.message}") }
    }
}


