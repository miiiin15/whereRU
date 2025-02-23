package com.miiiin15.whereru.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.miiiin15.whereru.common.utils.UUIDUtil
import com.miiiin15.whereru.data_resource.mapDataResource
import com.miiiin15.whereru.domain.model.PushMessage
import com.miiiin15.whereru.domain.model.PushType
import com.miiiin15.whereru.domain.model.ResponseType
import com.miiiin15.whereru.domain.session.AuthSessionManager
import com.miiiin15.whereru.domain.usecase.fcm.SendPushMessageUseCase
import com.miiiin15.whereru.domain.usecase.profile.GetPaginatedProfilesUseCase
import com.miiiin15.whereru.domain.usecase.profile.GetProfileUseCase
import com.miiiin15.whereru.domain.usecase.profile.UpdateProfileSessionIdUseCase
import com.miiiin15.whereru.domain.usecase.session.CreateSessionUseCase
import com.miiiin15.whereru.domain.usecase.session.DeleteSessionUseCase
import com.miiiin15.whereru.domain.usecase.session.ExitSessionUserCase
import com.miiiin15.whereru.domain.usecase.session.GetPaginatedSessionListUserCase
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
    private val getPaginatedProfilesUseCase: GetPaginatedProfilesUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getPaginatedSessionListUserCase: GetPaginatedSessionListUserCase,
    private val participationSessionUseCase: ParticipationSessionUseCase,
    private val exitSessionUseCase: ExitSessionUserCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase,
    private val updateProfileSessionIdUseCase: UpdateProfileSessionIdUseCase,
    private val sendPushMessageUseCase: SendPushMessageUseCase,
    private val authSessionManager: AuthSessionManager,
) : BaseViewModel<HomeViewModel.Event>() {

    private val _myProfile = MutableStateFlow<UserUiModel?>(null)
    val myProfile = _myProfile.asStateFlow()

    private val _sessionList = MutableStateFlow<List<JoinedSessionUiModel>>(emptyList())
    val sessionList = _sessionList.asStateFlow()

    private val _userList = MutableStateFlow<List<UserUiModel>>(emptyList())
    val userList = _userList.asStateFlow()

    private val _friendList = MutableStateFlow<List<UserUiModel>>(emptyList())
    val friendList = _friendList.asStateFlow()

    val fetched = MutableLiveData(false)


    private var lastSessionVisible: Long? = null // 마지막 값을 저장할 변수
    private var lastSessionPageSize = 15
    var hasMoreSessionData = true // 더 가저올 세션 데이터가 있나

    private var lastUserVisible: Long? = null // 마지막 값을 저장할 변수
    private val lastUserPageSize = 15
    var hasMoreUserData = true // 더 가저올 유저 데이터가 있나

    init {
        fetchList()
    }

    private fun fetchList() {
        loadPaginatedSessionList(true)
        loadPaginatedUserList(true)
        // TODO : 친구 목록 가져오기
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

    // 유저 목록 가져오기 nextPage : true면 다음 페이지, false면 초기화
    fun loadPaginatedUserList(nextPage: Boolean) = launch {
        if (!nextPage) {
            lastUserVisible = null
            hasMoreUserData = true
        } else if (!hasMoreUserData) return@launch

        getPaginatedProfilesUseCase(if (nextPage) lastUserVisible else null, lastUserPageSize)
            .mapDataResource { list ->
                if (list.isNotEmpty()) {
                    lastUserVisible = list.last().lastLoginAt
                }
                list.filter { it.userId != authSessionManager.uid }
                    .sortedByDescending { it.lastLoginAt }
                    .map { it.toPresentation() }
            }
            .collectDataResource({ result ->
                _userList.value = if (nextPage) {
                    _userList.value + result
                } else {
                    result
                }
                hasMoreUserData = result.size == lastUserPageSize
            })
    }

   // 최근 입장한 세션 목록 가져오기 nextPage : true면 다음 페이지, false면 초기화
    fun loadPaginatedSessionList(nextPage: Boolean) = launch {
        if (!nextPage) {
            lastSessionVisible = null
        }else if (!hasMoreSessionData) return@launch

        getPaginatedSessionListUserCase(
            authSessionManager.uid!!,
            if (nextPage) lastSessionVisible else null,
            lastUserPageSize
        ).mapDataResource { list ->
            if (list.isNotEmpty()) {
                lastSessionVisible = list.last().participationTime
            }
            list.sortedByDescending { it.participationTime }
                .map { it.toPresentation() }
        }.collectDataResource({ result ->
            _sessionList.value = if (nextPage) {
                _sessionList.value + result
            } else {
                result
            }
            hasMoreSessionData = result.size == lastSessionPageSize
        })
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
                event(Event.Navigate(HomeNavigationTarget.ToLiveLocation))
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
            event(Event.Navigate(HomeNavigationTarget.ToLiveLocation))
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
        fetched.value = false
    }

    sealed class Event : ViewEvent {
        data class Navigate(val target: HomeNavigationTarget) : Event()
    }
}