package com.miiiin15.whereru.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.miiiin15.whereru.common.utils.UUIDUtil
import com.miiiin15.whereru.data_resource.mapDataResource
import com.miiiin15.whereru.domain.model.PushMessage
import com.miiiin15.whereru.domain.model.PushType
import com.miiiin15.whereru.domain.model.ResponseType
import com.miiiin15.whereru.domain.session.AuthSessionManager
import com.miiiin15.whereru.domain.usecase.fcm.SendPushMessageUseCase
import com.miiiin15.whereru.domain.usecase.profile.GetAllProfilesUseCase
import com.miiiin15.whereru.domain.usecase.profile.GetProfileUseCase
import com.miiiin15.whereru.domain.usecase.profile.UpdateProfileSessionIdUseCase
import com.miiiin15.whereru.domain.usecase.session.CreateSessionUseCase
import com.miiiin15.whereru.domain.usecase.session.DeleteSessionUseCase
import com.miiiin15.whereru.domain.usecase.session.ExitSessionUserCase
import com.miiiin15.whereru.domain.usecase.session.GetRecentSessionListUseCase
import com.miiiin15.whereru.domain.usecase.session.ParticipationSessionUseCase
import com.miiiin15.whereru.presentation.base.BaseViewModel
import com.miiiin15.whereru.presentation.base.ViewEvent
import com.miiiin15.whereru.presentation.model.JoinedSessionUiModel
import com.miiiin15.whereru.presentation.model.PushMessageUiModel
import com.miiiin15.whereru.presentation.model.UserUiModel
import com.miiiin15.whereru.presentation.model.toPresentation
import com.miiiin15.whereru.presentation.navigation.HomeNavigationTarget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllProfilesUseCase: GetAllProfilesUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getRecentSessionListUseCase: GetRecentSessionListUseCase,
    private val participationSessionUseCase: ParticipationSessionUseCase,
    private val exitSessionUseCase: ExitSessionUserCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase,
    private val updateProfileSessionIdUseCase: UpdateProfileSessionIdUseCase,
    private val sendPushMessageUseCase: SendPushMessageUseCase,
    private val authSessionManager: AuthSessionManager,
) : BaseViewModel<HomeViewModel.Event>() {

    private val _navigationTarget = MutableStateFlow<HomeNavigationTarget?>(null)
    val navigationTarget = _navigationTarget.asStateFlow()

    private val _myProfile = MutableStateFlow<UserUiModel?>(null)
    val myProfile = _myProfile.asStateFlow()

    private val _sessionList = MutableStateFlow<List<JoinedSessionUiModel>>(emptyList())
    val sessionList = _sessionList.asStateFlow()

    private val _userList = MutableStateFlow<List<UserUiModel>>(emptyList())
    val userList = _userList.asStateFlow()

    private val _friendList = MutableStateFlow<List<UserUiModel>>(emptyList())
    val friendList = _friendList.asStateFlow()

    val fetched = MutableLiveData(false)

    init {
        fetchList()
    }

    fun fetchList() {
        getAllProfile()
        getRecentSessionList()
        // TODO : 친구 목록 가져오기
    }

    fun setNavigationTarget(target: HomeNavigationTarget) {
        _navigationTarget.value = target
    }

    // 내 프로필 가져오기
    fun fetchProfile() = launch {
        authSessionManager.uid?.let { uid ->
            getProfileUseCase(uid)
                .mapDataResource { it.toPresentation() }
                .collectDataResource(
                    onSuccess = { profile ->
                        authSessionManager.setTargetSessionId(profile.sessionId)
                        _myProfile.value = profile
                        fetched.value = true
                    },
                    loadingEnable = false
                )
        }
    }

    // 전체 유저 목록 가져오기
    fun getAllProfile() = launch {
        getAllProfilesUseCase()
            .mapDataResource { list ->
                list.filter { it.userId != authSessionManager.uid }
                    .sortedByDescending { it.lastLoginAt }
                    .map { it.toPresentation() }
            }
            .collectDataResource({
                _userList.value = it
            })
    }

    // 최근 입장한 세션 목록 가져오기
    fun getRecentSessionList() = launch {
        authSessionManager.uid.let { uid ->
            getRecentSessionListUseCase(uid!!).mapDataResource { list ->
                list.sortedByDescending { it.participationTime }
                    .map { it.toPresentation() }
            }
                .collectDataResource({
                    _sessionList.value = it
                })
        }
    }

    // 세션 참가 및 기록 저장
    fun participationSession(sessionId: String, hostNickname: String) {
        launch {
            participationSessionUseCase(
                authSessionManager.uid!!,
                sessionId,
                hostNickname,
                System.currentTimeMillis()
            ).collectDataResource({
                authSessionManager.setTargetSessionId(sessionId)
                setNavigationTarget(HomeNavigationTarget.ToLiveLocation)
            })
        }
    }

    // 세션 이탈 및 기록 삭제
    fun exitSession(sessionId: String) {
        launch {
            exitSessionUseCase(
                authSessionManager.uid!!,
                sessionId
            ).collectDataResource({
                _sessionList.value = _sessionList.value.filterNot { it.sessionId == sessionId }
            })
        }
    }

    // 세션 ID 체크 후 네비게이션 트리거
    fun checkSessionID(callback: (() -> Unit)? = null) {
        launch {
            if (authSessionManager.isEmptyTargetSessionId()) {
                createSession()
            }
            setNavigationTarget(HomeNavigationTarget.ToLiveLocation)
            callback?.invoke()
        }
    }

    // 세션 생성 - (성공 시)세션 ID 갱신
    private suspend fun createSession() {
        val userId = authSessionManager.uid
        val uuid = UUIDUtil.generateSessionId()
        createSessionUseCase(
            uuid,
            userId!!
        ).collectDataResource({
            updateSessionID(userId, uuid)
        })
    }

    // 세션 ID 저장 및 갱신
    private suspend fun updateSessionID(uid: String, sessionId: String) {
        updateProfileSessionIdUseCase(uid, sessionId).await()
        authSessionManager.setTargetSessionId(sessionId)

    }

    // 세션 삭제
    fun deleteSession() {
        val sessionId = authSessionManager.targetSessionId
        if (sessionId.isNullOrBlank()) return
        launch {
            deleteSessionUseCase(sessionId).collectDataResource({
                updateSessionID(authSessionManager.uid!!, "")
                _myProfile.value = _myProfile.value?.copy(sessionId = "")
            })
        }
    }

    // 위치 공유 세션 개설 요청 FCM 전송
    fun sendRequestLocationPushMessage(userData: UserUiModel) {
        launch {
            val message = PushMessage(
                type = PushType.REQUEST_LOCATION,
                fromUserId = _myProfile.value!!.userId,
                fromNickname = _myProfile.value!!.nickname,
                fromToken = _myProfile.value!!.fcmToken!!,
                toUserId = userData.userId,
                sessionId = "",
                timestamp = System.currentTimeMillis()
            )

            sendPushMessageUseCase(userData.fcmToken!!, message)
                .collectDataResource({
                    // TODO : FCM 전송 성공 시 처리
                })
        }
    }

    // 위치 공유 세션 수락/거절 FCM 전송
    fun sendResponsePushMessage(receivedMessage: PushMessageUiModel, accept: Boolean) {
        val responseType = if (accept) {
            ResponseType.ACCEPT
        } else {
            ResponseType.DECLINE
        }

        launch {
            val message = PushMessage(
                type = PushType.RESPONSE_LOCATION,
                fromUserId = _myProfile.value!!.userId,
                fromNickname = _myProfile.value!!.nickname,
                fromToken = _myProfile.value!!.fcmToken!!,
                toUserId = receivedMessage.fromUserId,
                sessionId = authSessionManager.targetSessionId!!,
                response = responseType,
                timestamp = System.currentTimeMillis()
            )

            sendPushMessageUseCase(receivedMessage.fromToken, message)
                .collectDataResource({
                    // TODO : FCM 전송 성공 시 처리
                })
        }
    }

    // 트리거 정리
    fun clearTrigger() {
        _navigationTarget.value = null
        fetched.value = false
    }

    sealed class Event : ViewEvent
}