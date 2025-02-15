package com.miiiin15.whereru.domain.usecase.auth

import com.miiiin15.whereru.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(email: String, password: String) =
        authRepository.register(email, password)
}