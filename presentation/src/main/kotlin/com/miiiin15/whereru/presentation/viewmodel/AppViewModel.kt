package com.miiiin15.whereru.presentation.viewmodel

import com.miiiin15.whereru.domain.session.AuthSessionManager
import com.miiiin15.whereru.presentation.base.BaseViewModel
import com.miiiin15.whereru.presentation.base.ViewEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authSessionManager: AuthSessionManager,
) : BaseViewModel<AppViewModel.Event>() {

    init {
        authSessionManager.clear()
    }

    sealed class Event : ViewEvent
}
