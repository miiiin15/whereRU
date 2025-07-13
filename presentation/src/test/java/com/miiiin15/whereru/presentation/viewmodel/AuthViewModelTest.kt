package com.miiiin15.whereru.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.miiiin15.whereru.data_resource.DataResource
import com.miiiin15.whereru.domain.session.AuthSessionManager
import com.miiiin15.whereru.domain.usecase.auth.LoginUseCase
import com.miiiin15.whereru.domain.usecase.auth.RegisterUserUseCase
import com.miiiin15.whereru.domain.usecase.profile.SetProfileUseCase
import com.miiiin15.whereru.domain.usecase.profile.UpdateLastLoginUseCase
import com.miiiin15.whereru.domain.usecase.profile.UpdateProfileFCMTokenUseCase
import com.miiiin15.whereru.local.model.AuthInfoModel
import com.miiiin15.whereru.local.pref.PrefUtil
import com.miiiin15.whereru.presentation.extension.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    // 테스트에 사용할 mock 객체 선언
    private lateinit var authViewModel: AuthViewModel
    private val loginUseCase: LoginUseCase = mockk()
    private val registerUserUseCase = mockk<RegisterUserUseCase>()
    private val setProfileUseCase = mockk<SetProfileUseCase>()
    private val updateFcmTokenUseCase = mockk<UpdateProfileFCMTokenUseCase>()
    private val updateLastLoginUseCase = mockk<UpdateLastLoginUseCase>()
    private val authSessionManager = mockk<AuthSessionManager>(relaxed = true)
    private val prefUtil = mockk<PrefUtil>(relaxed = true)

    // 코루틴 테스트를 위한 Rule
    // MainCoroutineRule을 사용하여 코루틴을 테스트할 때 메인 스레드를 설정 테스트가 끝난 후 메인 스레드를 원래대로 되돌림
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // LiveData 테스트를 위한 Rule
    // 이 Rule은 LiveData가 테스트 스레드에서 즉시 업데이트되도록 보장
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // 각 테스트 전에 ViewModel을 mock 객체로 초기화
    @Before
    fun setUp() {
        every { prefUtil.authInfoModel } returns null // autoLogin 방지
        authViewModel = AuthViewModel(
            loginUseCase,
            registerUserUseCase,
            setProfileUseCase,
            updateFcmTokenUseCase,
            updateLastLoginUseCase,
            authSessionManager,
            prefUtil
        )
    }

    // 로그인 성공 시 authState가 true로 변경되는지 검증
    @Test
    fun 로그인_성공시_상태_변경() {
        val email = "test@test.com"
        val password = "password"
        val uid = "fakeUid"

        // loginUseCase가 성공 결과를 반환하도록 mock 설정
        coEvery { loginUseCase(email, password) } returns flow { emit(DataResource.success(uid)) }
        coEvery { updateFcmTokenUseCase(uid, any()) } returns mockk(relaxed = true)
        coEvery { updateLastLoginUseCase(uid, any()) } returns mockk(relaxed = true)

        authViewModel.email.value = email
        authViewModel.password.value = password

        authViewModel.login()

        // authState가 true로 변경됐는지 확인
        assertEquals(true, authViewModel.authState.value)
        // 후속 작업이 올바르게 호출되었는지 검증
        verify { authSessionManager.login(uid) }
        verify { prefUtil.authInfoModel = AuthInfoModel(email, password) }
    }

    // 로그인 실패 시 authState가 false로 유지되는지 검증
    @Test
    fun 로그인_실패시_상태_유지() {
        val email = "test@test.com"
        val password = "wrong"

        // loginUseCase가 예외를 던지도록 mock 설정
        coEvery { loginUseCase(email, password) } returns flow { throw Exception("Login failed") }

        authViewModel.email.value = email
        authViewModel.password.value = password

        authViewModel.login()

        // authState가 false로 유지되는지 확인
        assertEquals(false,  authViewModel.authState.value)
        // 후속 작업이 호출되지 않았는지 검증
        verify(exactly = 0) { authSessionManager.login(any()) }
        verify(exactly = 0) { prefUtil.authInfoModel = AuthInfoModel(email, password) }
    }
}