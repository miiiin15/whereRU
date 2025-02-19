package com.miiiin15.whereru.presentation.viewmodel

import com.miiiin15.whereru.common.location.LocationTracker
import com.miiiin15.whereru.data_resource.mapDataResource
import com.miiiin15.whereru.domain.model.MyLocationData
import com.miiiin15.whereru.domain.session.AuthSessionManager
import com.miiiin15.whereru.domain.usecase.livelocation.DeleteMyLocationUseCase
import com.miiiin15.whereru.domain.usecase.profile.GetProfileUseCase
import com.miiiin15.whereru.domain.usecase.session.ObserveSessionUseCase
import com.miiiin15.whereru.domain.usecase.session.StopObserveSessionUseCase
import com.miiiin15.whereru.domain.usecase.livelocation.UpdateMyLocationUseCase
import com.miiiin15.whereru.presentation.base.BaseViewModel
import com.miiiin15.whereru.presentation.base.ViewEvent
import com.miiiin15.whereru.presentation.model.LiveLocationUserUiModel
import com.miiiin15.whereru.presentation.model.LocationSessionUiModel
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
    private val deleteMyLocationUseCase: DeleteMyLocationUseCase,
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

    private val _sessionInfo = MutableStateFlow<LocationSessionUiModel?>(null)
    val sessionInfo = _sessionInfo.asStateFlow()

    private val _users = MutableStateFlow<Map<String, LiveLocationUserUiModel>>(emptyMap())
    val users = _users.asStateFlow()

    private val _isHost = MutableStateFlow(false)
    val isHost = _isHost.asStateFlow()

    init {
        fetchProfile()
    }

    private fun fetchProfile() = launch {
        authSessionManager.uid?.let { uid ->
            getProfileUseCase(uid)
                .mapDataResource { it.toPresentation() }
                .collectDataResource({ profile ->
                    _myProfile.value = profile
                    _isHost.value = authSessionManager.uid == _sessionInfo.value?.hostId
                })
        }
    }

    fun setObserveSessionState(isObserving: Boolean) {
        val sessionId = authSessionManager.targetSessionId!!

        if (isObserving) {
            _isWantReceive.value = true
            launch {
                observeSessionUseCase(
                    sessionId,
                    onSessionUpdated = { session ->
                        if (!_isWantReceive.value) {
                            setObserveSessionState(false)
                        }

                        _sessionInfo.value = session.toPresentation()
                        val newUsers = session.users
                            .filterKeys { it != authSessionManager.uid }
                            .mapValues { entry -> entry.value.toPresentation() }
                        _users.value = newUsers
                        event(Event.UsersUpdated(newUsers))
                    },
                    onError = {
                        showAlert("${it.message}")
                        if (_sessionInfo.value?.hostId.isNullOrBlank()) {
                            setObserveSessionState(false)
                            setTrackingState(false)
                            return@observeSessionUseCase
                        }
                    }
                )
            }
        } else {
            launch {
                stopObserveSessionUseCase(sessionId).await()
                _isWantReceive.value = false
            }
        }
    }

    fun setTrackingState(isTracking: Boolean) {
        _isWantTransmit.value = isTracking

        if (isTracking) {
            locationTracker.startTracking { location ->
                updateMyLocation(
                    MyLocationData(
                        location.latitude,
                        location.longitude,
                        location.timestamp
                    )
                )
            }
        } else {
            locationTracker.stopTracking()
        }
    }

    private fun updateMyLocation(location: MyLocationData) {
        if (!_isWantTransmit.value) return
        if (_sessionInfo.value?.hostId.isNullOrBlank()) {
            showAlert("세션이 존재하지 않습니다.")
            setObserveSessionState(false)
            setTrackingState(false)
            return
        }
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

    fun deleteMyLocation(callback: () -> Unit) {
        setTrackingState(false)
        launch {
            deleteMyLocationUseCase(
                authSessionManager.targetSessionId!!,
                authSessionManager.uid!!
            ).collectDataResource(
                onSuccess = {
                    callback.invoke()
                },
                loadingEnable = false
            )
        }
    }

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

    sealed class Event : ViewEvent {
        data class UsersUpdated(val users: Map<String, LiveLocationUserUiModel>) : Event()
    }
}