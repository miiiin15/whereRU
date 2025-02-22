package com.miiiin15.whereru.remote.impl

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
import com.miiiin15.whereru.remote.utils.deleteDocument
import com.miiiin15.whereru.remote.utils.getCollection
import com.miiiin15.whereru.remote.utils.getDocument
import com.miiiin15.whereru.remote.utils.getPaginatedDocuments
import com.miiiin15.whereru.remote.utils.setDocument
import com.miiiin15.whereru.remote.utils.updateDocument
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
        profileImageUrl: String?,
        location: MyLocationEntity
    ): Unit {

        val myLocation = MyLocationRequest(
            nickname = nickname,
            profileImageUrl = profileImageUrl,
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
        return firebaseFirestore.getCollection<JoinedSessionEntity>(
            "${FirebasePaths.JOINED_SESSIONS}/$userId/sessions", "세션 조회 실패"
        )
    }

    /**
     * 최근 참여한 세션 범위 조회
     * **/
    override suspend fun getPaginatedSessionList(
        userId: String,
        lastVisible: Long?,
        pageSize: Int
    ): List<JoinedSessionEntity> {
        return firebaseFirestore.getPaginatedDocuments<JoinedSessionEntity>(
            collectionPath = "${FirebasePaths.JOINED_SESSIONS}/$userId/sessions",
            orderByField = "participationTime",
            lastVisible = lastVisible,
            pageSize = pageSize,
            errorLabel = "세션 범위 조회 실패"
        )
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
        val participationRequest = ParticipationSessionRequest(hostNickname, participationTime)

        firebaseFirestore.setDocument(
            "${FirebasePaths.JOINED_SESSIONS}/$userId/sessions",
            targetSessionId,
            participationRequest,
            "세션 기록 실패"
        )
    }

    /**
     * 세션 이탈
     * **/
    override suspend fun exitSession(userId: String, targetSessionId: String): Unit {
        firebaseFirestore.deleteDocument(
            "${FirebasePaths.JOINED_SESSIONS}/$userId/sessions",
            targetSessionId,
            "세션 이탈 실패"
        )
    }


    /**
     * 모든 프로필 조회
     * **/
    override suspend fun getAllProfiles(): List<ProfileEntity> {
        return firebaseFirestore.getCollection<ProfileEntity>(
            FirebasePaths.USER_PROFILE,
            "프로필 조회 실패"
        )
    }

    /**
     * 모든 프로필 범위 조회
     * **/
    override suspend fun getPaginatedProfiles(
        lastVisible: Long?,
        pageSize: Int
    ): List<ProfileEntity> {
        return firebaseFirestore.getPaginatedDocuments<ProfileEntity>(
            collectionPath = FirebasePaths.USER_PROFILE,
            orderByField = "lastLoginAt",
            lastVisible = lastVisible,
            pageSize = pageSize,
            errorLabel = "프로필 목록 조회 실패"
        )
    }

    /**
     * 단일 프로필 조회
     * **/
    override suspend fun getProfile(userId: String): ProfileEntity {
        return firebaseFirestore.getDocument<ProfileEntity>(
            FirebasePaths.USER_PROFILE,
            userId,
            "프로필 조회 실패"
        )!!
    }

    /**
     * 프로필 저장
     * **/
    override suspend fun setProfile(profile: ProfileEntity): Unit {
        firebaseFirestore.setDocument(
            FirebasePaths.USER_PROFILE, profile.userId, profile, "프로필 저장 실패"
        )
    }

    /**
     * 프로필 세션 ID 업데이트
     * **/
    override suspend fun updateProfileSessionId(userId: String, sessionId: String) {
        firebaseFirestore.updateDocument(
            FirebasePaths.USER_PROFILE,
            userId,
            hashMapOf("sessionId" to sessionId),
            "프로필 세션 ID 갱신 실패"
        )
    }

    /**
     * 마지막 로그인 시간 업데이트
     * **/
    override suspend fun updateLastLogin(userId: String, lastLoginAt: Long) {
        firebaseFirestore.updateDocument(
            FirebasePaths.USER_PROFILE, userId, hashMapOf("lastLoginAt" to lastLoginAt),
            "마지막 로그인 시간 갱신 실패"
        )
    }

    /**
     * FCM 토큰 업데이트
     * **/
    override suspend fun updateFcmToken(userId: String, fcmToken: String) {
        firebaseFirestore.updateDocument(
            FirebasePaths.USER_PROFILE, userId, hashMapOf("fcmToken" to fcmToken),
            "FCM 토큰 갱신 실패"
        )
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
                    val profileImageUrl =
                        userSnap.child("profileImageUrl").getValue(String::class.java) ?: ""
                    val latitude =
                        userSnap.child("location/latitude").getValue(Double::class.java) ?: 0.0
                    val longitude =
                        userSnap.child("location/longitude").getValue(Double::class.java) ?: 0.0
                    val timestamp =
                        userSnap.child("location/timestamp").getValue(Long::class.java) ?: 0L

                    val user = LiveLocationUserBlock(
                        userId = userId,
                        nickname = nickname,
                        profileImageUrl = profileImageUrl,
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


