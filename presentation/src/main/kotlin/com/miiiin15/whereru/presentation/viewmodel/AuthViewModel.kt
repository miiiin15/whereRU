package com.miiiin15.whereru.presentation.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.miiiin15.whereru.domain.session.AuthSessionManager
import com.miiiin15.whereru.domain.usecase.auth.LoginUseCase
import com.miiiin15.whereru.domain.usecase.auth.RegisterUserUseCase
import com.miiiin15.whereru.domain.usecase.profile.SetProfileUseCase
import com.miiiin15.whereru.domain.usecase.profile.UpdateLastLoginUseCase
import com.miiiin15.whereru.domain.usecase.profile.UpdateProfileFCMTokenUseCase
import com.miiiin15.whereru.local.model.AuthInfoModel
import com.miiiin15.whereru.local.pref.PrefUtil
import com.miiiin15.whereru.presentation.base.BaseViewModel
import com.miiiin15.whereru.presentation.base.ViewEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val setProfileUseCase: SetProfileUseCase,
    private val updateFcmTokenUseCase: UpdateProfileFCMTokenUseCase,
    private val updateLastLoginUseCase: UpdateLastLoginUseCase,
    private val authSessionManager: AuthSessionManager,
    private val prefUtil: PrefUtil
) : BaseViewModel<AuthViewModel.Event>() {

    private val _authState = MutableStateFlow(false)
    val authState = _authState.asStateFlow()

    private val _fcmToken = MutableStateFlow<String?>(null)
    val fcmToken = _fcmToken.asStateFlow()

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    val isButtonEnabled = MediatorLiveData<Boolean>().apply {
        addSource(email) { validateInputs() }
        addSource(password) { validateInputs() }
    }

    init {
        autoLogin()
    }

    private fun validateInputs() {
        val emailVal = email.value.orEmpty()
        val passwordVal = password.value.orEmpty()
        isButtonEnabled.value = emailVal.isNotBlank() && passwordVal.isNotBlank()
    }

    fun setFcmToken(token: String?) {
        _fcmToken.value = token
    }

    fun login() {
        launch {
            loginUseCase(email.value!!, password.value!!)
                .collectDataResource({
                    updateInfo(it)
                    _authState.value = true
                    authSessionManager.login(it)
                    if (prefUtil.authInfoModel == null) prefUtil.authInfoModel =
                        (AuthInfoModel(email.value!!, password.value!!))
                })
        }
    }

    fun register() {
        launch {
            registerUserUseCase(email.value!!, password.value!!)
                .collectDataResource({
                    setInitialProfile(it)
                    login()
                })
        }
    }

    // 프로필 기본값 세팅
    private suspend fun setInitialProfile(uid: String) {
        setProfileUseCase(
            userId = uid,
            nickname = "유저_${uid.substring(0, 8)}",
            lastLoginAt = System.currentTimeMillis(),
            fcmToken = _fcmToken.value
        ).await()
    }

    // 마지막 로그인 시간 , fcmToken 갱신
    private fun updateInfo(uid: String) {
        launch {
            updateFcmTokenUseCase(uid, _fcmToken.value ?: "")
            updateLastLoginUseCase(uid, System.currentTimeMillis())
        }
    }

    // 자동 로그인
    private fun autoLogin() {
        prefUtil.authInfoModel?.let {
            launch {
                loginUseCase(it.email, it.password)
                    .collectDataResource(
                        onSuccess = {
                            updateInfo(it)
                            _authState.value = true
                            authSessionManager.login(it)
                        },
                        onError = {
                            hideLoading()
                            prefUtil.clearAuthInfoModel()
                        }
                    )
            }
        }
    }

    sealed class Event : ViewEvent
}
