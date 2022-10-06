package com.andikas.storyapp.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import com.andikas.storyapp.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import javax.inject.Inject

private const val TAG: String = "UserPreferencesRepo"

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<UserPreferences>
) {

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading sort order preferences.", exception)
                emit(UserPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateUserId(userId: String?) {
        dataStore.updateData { preferences ->
            preferences.toBuilder().setUserId(userId).build()
        }
    }

    suspend fun updateUserName(name: String?) {
        dataStore.updateData { preferences ->
            preferences.toBuilder().setName(name).build()
        }
    }

    suspend fun updateUserToken(token: String?) {
        dataStore.updateData { preferences ->
            preferences.toBuilder().setToken(token).build()
        }
    }

    suspend fun clearUserData() {
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .clear()
                .build()
        }
    }

}