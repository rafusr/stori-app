package com.andikas.storyapp.ui.splash

import androidx.lifecycle.MutableLiveData
import com.andikas.storyapp.UserPreferences
import com.andikas.storyapp.base.BaseViewModel
import com.andikas.storyapp.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : BaseViewModel() {

    val userPrefs = MutableLiveData<UserPreferences>()

    suspend fun getUserDetail() {
        userPreferencesRepository.userPreferencesFlow.collect { userPrefs.postValue(it) }
    }

}