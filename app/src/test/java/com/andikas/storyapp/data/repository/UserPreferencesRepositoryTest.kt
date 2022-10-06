package com.andikas.storyapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.lifecycle.asLiveData
import com.andikas.storyapp.DataDummy
import com.andikas.storyapp.MainDispatcherRule
import com.andikas.storyapp.UserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserPreferencesRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var testDataStore: DataStore<UserPreferences>
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    @Before
    fun setUp() {
        userPreferencesRepository = UserPreferencesRepository(testDataStore)
    }

    @Test
    fun `when updateUserId Should Return Success`() = runTest {
        val dummyUserId = DataDummy.generateDummyLoginResponse().loginResult?.userId
        userPreferencesRepository.updateUserId(dummyUserId)
        userPreferencesRepository.userPreferencesFlow.asLiveData().observeForever {
            assertEquals(it.userId, dummyUserId)
        }
    }

    @Test
    fun `when updateUserToken Should Return Success`() = runTest {
        val dummyUserToken = DataDummy.generateDummyLoginResponse().loginResult?.token
        userPreferencesRepository.updateUserToken(dummyUserToken)
        userPreferencesRepository.userPreferencesFlow.asLiveData().observeForever {
            assertEquals(it.token, dummyUserToken)
        }
    }

    @Test
    fun `when updateUserName Should Return Success`() = runTest {
        val dummyUserName = DataDummy.generateDummyLoginResponse().loginResult?.name
        userPreferencesRepository.updateUserName(dummyUserName)
        userPreferencesRepository.userPreferencesFlow.asLiveData().observeForever {
            assertEquals(it.name, dummyUserName)
        }
    }
}