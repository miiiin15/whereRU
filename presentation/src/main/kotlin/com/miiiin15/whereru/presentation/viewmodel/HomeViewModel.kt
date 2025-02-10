package com.miiiin15.whereru.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.miiiin15.whereru.data.utils.AuthSessionManager
import com.miiiin15.whereru.data_resource.mapDataResource
import com.miiiin15.whereru.domain.usecase.GetProfileUseCase
import com.miiiin15.whereru.presentation.base.BaseViewModel
import com.miiiin15.whereru.presentation.base.ViewEvent
import com.miiiin15.whereru.presentation.model.UserUiModel
import com.miiiin15.whereru.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val authSessionManager: AuthSessionManager,
) : BaseViewModel<HomeViewModel.Event>() {

    private val _myProfile = MutableStateFlow<UserUiModel?>(null)
    val myProfile = _myProfile.asStateFlow()

    val fetched = MutableLiveData<Boolean>(false)

    fun fetchProfile() = launch {
        authSessionManager.getUid()?.let { uid ->
            getProfileUseCase(uid)
                .mapDataResource { it.toPresentation() }
                .collectDataResource({ profile ->
                    _myProfile.value = profile
                    fetched.value = true
                })
        }
    }

    sealed class Event : ViewEvent
}