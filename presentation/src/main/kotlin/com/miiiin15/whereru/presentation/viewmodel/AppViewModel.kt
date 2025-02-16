package com.miiiin15.whereru.presentation.viewmodel

import com.miiiin15.whereru.domain.session.AuthSessionManager
import com.miiiin15.whereru.domain.usecase.fcm.GetFCMTokenUseCase
import com.miiiin15.whereru.presentation.base.BaseViewModel
import com.miiiin15.whereru.presentation.base.ViewEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val fcmTokenUseCase: GetFCMTokenUseCase,
    private val authSessionManager: AuthSessionManager,
) : BaseViewModel<AppViewModel.Event>() {

    private val _fcmToken = MutableStateFlow<String?>(null)
    val fcmToken = _fcmToken.asStateFlow()

    init {
        launch {
            authSessionManager.clear()
            getFCMToken()
        }
    }

    suspend fun getFCMToken() {
        val token = fcmTokenUseCase().await()
        _fcmToken.value = token
    }

    sealed class Event : ViewEvent
}
