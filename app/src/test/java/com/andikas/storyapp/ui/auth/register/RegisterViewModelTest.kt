package com.andikas.storyapp.ui.auth.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.andikas.storyapp.DataDummy
import com.andikas.storyapp.MainDispatcherRule
import com.andikas.storyapp.data.repository.AuthRepository
import com.andikas.storyapp.data.source.remote.response.BaseResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var registerViewModel: RegisterViewModel

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(authRepository)
    }

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Test
    fun `when userRegister Should call userRegister And Return Success`() = runTest {
        val dummyBody = DataDummy.generateDummyRegisterAuthBody()
        val dummyResponse = DataDummy.generateDummySuccessResponse()
        val expectedResponse: Flow<BaseResponse> = flowOf(dummyResponse)

        `when`(authRepository.userRegister(dummyBody, null)).thenReturn(expectedResponse)

        registerViewModel.userRegister(
            dummyBody.name as String,
            dummyBody.email,
            dummyBody.password,
            null
        )
        verify(authRepository).userRegister(dummyBody, null)
        authRepository.userRegister(dummyBody, null).collectLatest {
            assertFalse(it.error)
            assertEquals(dummyResponse.message, it.message)
        }
    }

}