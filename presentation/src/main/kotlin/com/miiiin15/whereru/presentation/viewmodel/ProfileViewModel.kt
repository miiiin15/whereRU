package com.miiiin15.whereru.presentation.viewmodel

import com.miiiin15.whereru.presentation.base.BaseViewModel
import com.miiiin15.whereru.presentation.base.ViewEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    // TODO : UseCase 추가
) : BaseViewModel<ProfileViewModel.Event>() {

    sealed class Event : ViewEvent
}
