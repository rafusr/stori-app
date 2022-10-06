package com.andikas.storyapp.ui.auth.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import com.andikas.storyapp.DataDummy
import com.andikas.storyapp.MainDispatcherRule
import com.andikas.storyapp.UserPreferences
import com.andikas.storyapp.data.repository.AuthRepository
import com.andikas.storyapp.data.repository.UserPreferencesRepository
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
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var testDataStore: DataStore<UserPreferences>
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        userPreferencesRepository = UserPreferencesRepository(testDataStore)
        loginViewModel = LoginViewModel(authRepository, userPreferencesRepository)
    }

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Test
    fun `when userLogin Should call userLogin And Return Success`() = runTest {
        val dummyBody = DataDummy.generateDummyLoginAuthBody()
        val dummyResponse = DataDummy.generateDummyLoginResponse()
        val expectedResponse: Flow<BaseResponse> = flowOf(dummyResponse)

        `when`(authRepository.userLogin(dummyBody, null)).thenReturn(expectedResponse)

        loginViewModel.userLogin(dummyBody.email, dummyBody.password, null)

        verify(authRepository).userLogin(dummyBody, null)

        authRepository.userLogin(dummyBody, null).collectLatest {
            assertFalse(it.error)
            assertEquals(dummyResponse.message, it.message)
            assertEquals(dummyResponse.loginResult, it.loginResult)
        }
    }

}