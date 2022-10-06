package com.andikas.storyapp.data.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.andikas.storyapp.UserPreferences
import com.andikas.storyapp.data.source.local.preference.UserPreferencesSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TEST_DATA_STORE_FILE_NAME: String = "test_user_prefs.pb"

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UserPreferencesRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testCoroutineDispatcher = UnconfinedTestDispatcher()
    private val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())
    private val testContext: Context = ApplicationProvider.getApplicationContext()
    private val testDataStore: DataStore<UserPreferences> =
        DataStoreFactory.create(
            serializer = UserPreferencesSerializer,
            scope = testCoroutineScope,
            produceFile = { testContext.dataStoreFile(TEST_DATA_STORE_FILE_NAME) }
        )
    private val userPreferencesRepository: UserPreferencesRepository =
        UserPreferencesRepository(testDataStore)

    @Before
    fun setup() {
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @Test
    fun writeUserIdReturnSuccess() = runTest {
        val dummyUserId = "dummyUserId"
        val expectedUserId = "dummyUserId"
        userPreferencesRepository.updateUserId(dummyUserId)
        assertEquals(userPreferencesRepository.userPreferencesFlow.first().userId, expectedUserId)
    }

    @Test
    fun writeUserTokenReturnSuccess() = runTest {
        val dummyToken = "dummyUserToken"
        val expectedToken = "dummyUserToken"
        userPreferencesRepository.updateUserToken(dummyToken)
        assertEquals(userPreferencesRepository.userPreferencesFlow.first().token, expectedToken)
    }

    @Test
    fun writeUserNameReturnSuccess() = runTest {
        val dummyName = "dummyUserName"
        val expectedName = "dummyUserName"
        userPreferencesRepository.updateUserName(dummyName)
        assertEquals(userPreferencesRepository.userPreferencesFlow.first().name, expectedName)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        testCoroutineScope.runTest { userPreferencesRepository.clearUserData() }
        testCoroutineScope.cancel()
    }
}