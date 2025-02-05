package com.miiiin15.whereru.presentation.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.miiiin15.whereru.data_resource.mapDataResource
import com.miiiin15.whereru.domain.usecase.LoginUseCase
import com.miiiin15.whereru.domain.usecase.RegisterUserUseCase
import com.miiiin15.whereru.presentation.base.BaseViewModel
import com.miiiin15.whereru.presentation.base.ViewEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUserUseCase: RegisterUserUseCase
) : BaseViewModel<AuthViewModel.Event>() {

    private val _authState = MutableStateFlow<String>("")
    val authState = _authState.asStateFlow()

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    val isButtonEnabled = MediatorLiveData<Boolean>().apply {
        addSource(email) { validateInputs() }
        addSource(password) { validateInputs() }
    }

    private fun validateInputs() {
        val emailVal = email.value.orEmpty()
        val passwordVal = password.value.orEmpty()
        isButtonEnabled.value = emailVal.isNotBlank() && passwordVal.isNotBlank()
    }


    fun register() {
        launch {
            _authState.value = registerUserUseCase(email.value!!, password.value!!)
                .mapDataResource { it }
                .await() ?: return@launch
        }
    }

    fun login() {
        launch {
            _authState.value = loginUseCase(email.value!!, password.value!!)
                .mapDataResource { it }
                .await() ?: return@launch
        }
    }


    sealed class Event : ViewEvent
}
