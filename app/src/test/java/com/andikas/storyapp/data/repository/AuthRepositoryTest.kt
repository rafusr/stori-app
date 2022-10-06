package com.andikas.storyapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.andikas.storyapp.DataDummy
import com.andikas.storyapp.MainDispatcherRule
import com.andikas.storyapp.data.FakeApiService
import com.andikas.storyapp.data.source.remote.client.ApiService
import com.skydoves.sandwich.getOrElse
import com.skydoves.sandwich.isSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        apiService = FakeApiService()
    }

    @Test
    fun `when userRegister Should Return Success`() = runTest {
        val userRegister = apiService.userRegister(null)
        Assert.assertTrue(userRegister.isSuccess)
    }

    @Test
    fun `when userLogin Should Not Null and Return Success`() = runTest {
        val userLogin = apiService.userLogin(null)
        val expectedLoginResponse = DataDummy.generateDummyLoginResponse()
        val actualLoginResponse = userLogin.getOrElse { expectedLoginResponse }.loginResult
        Assert.assertTrue(userLogin.isSuccess)
        Assert.assertEquals(expectedLoginResponse.loginResult?.name, actualLoginResponse?.name)
        Assert.assertEquals(expectedLoginResponse.loginResult?.token, actualLoginResponse?.token)
        Assert.assertEquals(expectedLoginResponse.loginResult?.userId, actualLoginResponse?.userId)
    }

}