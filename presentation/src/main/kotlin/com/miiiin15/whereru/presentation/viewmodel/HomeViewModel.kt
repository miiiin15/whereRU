package com.miiiin15.whereru.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.miiiin15.whereru.common.utils.UUIDUtil
import com.miiiin15.whereru.data_resource.mapDataResource
import com.miiiin15.whereru.domain.session.AuthSessionManager
import com.miiiin15.whereru.domain.usecase.CreateSessionUseCase
import com.miiiin15.whereru.domain.usecase.ExitSessionUserCase
import com.miiiin15.whereru.domain.usecase.GetAllProfilesUseCase
import com.miiiin15.whereru.domain.usecase.GetProfileUseCase
import com.miiiin15.whereru.domain.usecase.ParticipationSessionUseCase
import com.miiiin15.whereru.domain.usecase.UpdateProfileSessionIdUseCase
import com.miiiin15.whereru.presentation.base.BaseViewModel
import com.miiiin15.whereru.presentation.base.ViewEvent
import com.miiiin15.whereru.presentation.model.LocationSessionUiModel
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
    private val participationSessionUseCase: ParticipationSessionUseCase,
    private val exitSessionUseCase: ExitSessionUserCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val updateProfileSessionIdUseCase: UpdateProfileSessionIdUseCase,
    private val authSessionManager: AuthSessionManager,
) : BaseViewModel<HomeViewModel.Event>() {

    private val _navigationTarget = MutableStateFlow<HomeNavigationTarget?>(null)
    val navigationTarget = _navigationTarget.asStateFlow()

    private val _myProfile = MutableStateFlow<UserUiModel?>(null)
    val myProfile = _myProfile.asStateFlow()

    private val _sessionList = MutableStateFlow<List<LocationSessionUiModel>>(emptyList())
    val sessionList = _sessionList.asStateFlow()

    private val _userList = MutableStateFlow<List<UserUiModel>>(emptyList())
    val userList = _userList.asStateFlow()

    private val _friendList = MutableStateFlow<List<UserUiModel>>(emptyList())
    val friendList = _friendList.asStateFlow()


    val fetched = MutableLiveData<Boolean>(false)

    init {
        getAllProfile()
    }

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

    // 세션 참가
    fun participationSession(sessionId: String, hostNickname: String) {
        launch {
            participationSessionUseCase(
                authSessionManager.uid!!,
                sessionId,
                hostNickname,
                System.currentTimeMillis()
            ).collectDataResource({
                authSessionManager.setTargetSessionId(sessionId)
                _navigationTarget.value = HomeNavigationTarget.ToLiveLocation
            })
        }
    }

    // 세션 탈퇴
    fun exitSession(sessionId: String) {
        launch {
            exitSessionUseCase(
                authSessionManager.uid!!,
                sessionId
            ).collectDataResource({
                // TODO: 세션 탈퇴 후 처리
            })
        }
    }

    // 세션 ID 체크 후 네비게이션 트리거
    fun checkSessionID() {
        launch {
            if (authSessionManager.isEmptyTargetSessionId()) {
                createSession()
            }
            _navigationTarget.value = HomeNavigationTarget.ToLiveLocation
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

    // 트리거 정리
    fun clearTrigger() {
        _navigationTarget.value = null
        fetched.value = false
    }

    sealed class Event : ViewEvent
}