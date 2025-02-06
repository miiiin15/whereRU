package com.miiiin15.whereru.presentation.viewmodel

import com.miiiin15.whereru.common.utils.AuthSessionManager
import com.miiiin15.whereru.presentation.base.BaseViewModel
import com.miiiin15.whereru.presentation.base.ViewEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    // TODO : UseCase 추가
) : BaseViewModel<AppViewModel.Event>() {
    val authSessionManager = AuthSessionManager()

    init {
        authSessionManager.logout()
    }

    sealed class Event : ViewEvent
}
