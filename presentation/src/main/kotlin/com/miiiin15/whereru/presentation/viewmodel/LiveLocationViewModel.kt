package com.miiiin15.whereru.presentation.viewmodel

import com.miiiin15.whereru.common.location.LocationTracker
import com.miiiin15.whereru.data_resource.mapDataResource
import com.miiiin15.whereru.domain.model.MyLocationData
import com.miiiin15.whereru.domain.session.AuthSessionManager
import com.miiiin15.whereru.domain.usecase.GetProfileUseCase
import com.miiiin15.whereru.domain.usecase.ObserveSessionUseCase
import com.miiiin15.whereru.domain.usecase.StopObserveSessionUseCase
import com.miiiin15.whereru.domain.usecase.UpdateMyLocationUseCase
import com.miiiin15.whereru.presentation.base.BaseViewModel
import com.miiiin15.whereru.presentation.base.ViewEvent
import com.miiiin15.whereru.presentation.model.LocationUiModel
import com.miiiin15.whereru.presentation.model.UserUiModel
import com.miiiin15.whereru.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LiveLocationViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateMyLocationUseCase: UpdateMyLocationUseCase,
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val stopObserveSessionUseCase: StopObserveSessionUseCase,
    private val locationTracker: LocationTracker,
    private val authSessionManager: AuthSessionManager,
) : BaseViewModel<LiveLocationViewModel.Event>() {

    private val _myProfile = MutableStateFlow<UserUiModel?>(null)
    val myProfile = _myProfile.asStateFlow()

    private val _myLocation = MutableStateFlow<LocationUiModel?>(null)
    val myLocation = _myLocation.asStateFlow()

    private val _isWantTransmit = MutableStateFlow(true)
    val isWantTransmit = _isWantTransmit.asStateFlow()

    private val _isWantReceive = MutableStateFlow(true)
    val isWantReceive = _isWantReceive.asStateFlow()


    init {
        fetchProfile()
    }

    private fun fetchProfile() = launch {
        authSessionManager.uid?.let { uid ->
            getProfileUseCase(uid)
                .mapDataResource { it.toPresentation() }
                .collectDataResource({ profile ->
                    authSessionManager.setTargetSessionId(profile.sessionId)
                    _myProfile.value = profile
                })
        }
    }


    // 세션 정보 구독 시작, 중지
    fun setObserveSessionState(isObserving: Boolean) {
        val sessionId = authSessionManager.targetSessionId!!

        if (isObserving) { // 시작
            _isWantReceive.value = true
            launch {
                observeSessionUseCase(
                    sessionId,
                    onSessionUpdated = {
                        if (!_isWantReceive.value) {
                            setObserveSessionState(false)
                        }
                        // TODO : 세션 정보 받은 후 로직
                    },
                    onError = { showAlert("${it.message}") }
                )
            }
        } else { // 중지
            launch {
                stopObserveSessionUseCase(sessionId).await()
                        _isWantReceive.value = false
            }
        }
    }

    // 트래킹 시작, 중단
    fun setTrackingState(isTracking: Boolean) {
        _isWantTransmit.value = isTracking

        if (isTracking) { // 시작
            locationTracker.startTracking { location ->
                updateMyLocation(
                    MyLocationData(
                        location.latitude,
                        location.longitude,
                        location.timestamp
                    )
                )
            }
        } else { // 중지
            locationTracker.stopTracking()
        }
    }

    // 세션에 내 위치 정보 업로드
    private fun updateMyLocation(location: MyLocationData) {
        if (!_isWantTransmit.value) return
        launch {
            _myProfile.value?.nickname.let {
                updateMyLocationUseCase(
                    authSessionManager.targetSessionId!!,
                    authSessionManager.uid!!,
                    _myProfile.value!!.nickname,
                    location
                ).collectDataResource(
                    onSuccess = {},
                    loadingEnable = false
                )
            }
        }
    }

    // 단발성 위치 추적
    fun currentMyLocation() {
        launch {
            val loc = locationTracker.getCurrentLocation()
            loc?.let { location ->
                _myLocation.value = location.toPresentation()
            } ?: return@launch
        }
    }

    override fun onCleared() {
        super.onCleared()
        setObserveSessionState(false)
        setTrackingState(false)
    }

    sealed class Event : ViewEvent
}
