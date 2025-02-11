package com.miiiin15.whereru.presentation.viewmodel

import com.miiiin15.whereru.common.location.LocationTracker
import com.miiiin15.whereru.data_resource.mapDataResource
import com.miiiin15.whereru.domain.model.MyLocationData
import com.miiiin15.whereru.domain.session.AuthSessionManager
import com.miiiin15.whereru.domain.usecase.GetProfileUseCase
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
    private val locationTracker: LocationTracker,
    private val authSessionManager: AuthSessionManager,
) : BaseViewModel<LiveLocationViewModel.Event>() {

    private val _myProfile = MutableStateFlow<UserUiModel?>(null)
    val myProfile = _myProfile.asStateFlow()

    private val _myLocation = MutableStateFlow<LocationUiModel?>(null)
    val myLocation = _myLocation.asStateFlow()


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

    // 세션에 위치 정보 업데이트
    fun updateMyLocation(location: MyLocationData) {
        launch {
            _myProfile.value?.nickname.let {
                updateMyLocationUseCase(
                    authSessionManager.targetSessionId!!,
//                    authSessionManager.uid!!,
                    "7f9d3e12a47c48bfb2c31a90d9ba",
                    _myProfile.value!!.nickname,
                    location
                ).collectDataResource(
                    onSuccess = {},
                    loadingEnable = false
                )
            }
        }
    }

    // 트래킹 시작
    fun startTrackingMyLocation() {
        locationTracker.startTracking { location ->
            updateMyLocation(
                MyLocationData(
                    location.latitude,
                    location.longitude,
                    location.timestamp
                )
            )
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

    // 트래킹 중단
    fun stopTrackingMyLocation() {
        println("❌트래커 중지")
        locationTracker.stopTracking()
    }

    override fun onCleared() {
        super.onCleared()
        stopTrackingMyLocation()
    }

    sealed class Event : ViewEvent
}
