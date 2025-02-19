package com.miiiin15.whereru.presentation.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.miiiin15.whereru.data_resource.mapDataResource
import com.miiiin15.whereru.domain.session.AuthSessionManager
import com.miiiin15.whereru.domain.usecase.profile.GetProfileUseCase
import com.miiiin15.whereru.domain.usecase.profile.SetProfileUseCase
import com.miiiin15.whereru.presentation.base.BaseViewModel
import com.miiiin15.whereru.presentation.base.ViewEvent
import com.miiiin15.whereru.presentation.model.UserUiModel
import com.miiiin15.whereru.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val setProfileUseCase: SetProfileUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val authSessionManager: AuthSessionManager,
) : BaseViewModel<ProfileViewModel.Event>() {

    private val _selectedColor = MutableStateFlow<Int?>(null)
    val selectedColor = _selectedColor.asStateFlow()

    private val _myProfile = MutableStateFlow<UserUiModel?>(null)
    val myProfile = _myProfile.asStateFlow()

    val nickname = MutableLiveData("")

    init {
        fetchProfile()
    }

    fun setSelectedColor(color: Int) {
        _selectedColor.value = color
    }

    private fun fetchProfile() = launch {
        authSessionManager.uid?.let { uid ->
            getProfileUseCase(uid)
                .mapDataResource { it.toPresentation() }
                .collectDataResource({ profile ->
                    _myProfile.value = profile
                    nickname.value = profile.nickname
                })
        }
    }

    fun setProfile(callback: (() -> Unit)) = launch {
        val uid = authSessionManager.uid!!
        setProfileUseCase(
            userId = uid,
            nickname = nickname.value.takeIf { !it.isNullOrBlank() } ?: "유저_${uid.substring(0, 6)}",
            profileImageUrl = selectedColor.value.toString(),
            lastLoginAt = System.currentTimeMillis(),
            fcmToken = myProfile.value?.fcmToken
        ).collectDataResource({
            callback.invoke()
        })
    }

    sealed class Event : ViewEvent
}
