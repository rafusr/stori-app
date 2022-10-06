package com.andikas.storyapp.ui.auth.login

import com.andikas.storyapp.base.BaseViewModel
import com.andikas.storyapp.data.repository.AuthRepository
import com.andikas.storyapp.data.repository.UserPreferencesRepository
import com.andikas.storyapp.data.source.remote.response.auth.AuthBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : BaseViewModel() {

    suspend fun userLogin(
        email: String,
        password: String,
        onError: ((message: String?) -> Unit)? = { errorMessage.postValue(it) }
    ) {
        val authBody = AuthBody(email = email, password = password)
        authRepository.userLogin(authBody, onError)
            .onStart { isLoading.postValue(true) }
            .onCompletion { isLoading.postValue(false) }
            .collect {
                if (it.loginResult != null) userPreferencesRepository.apply {
                    updateUserId(it.loginResult.userId)
                    updateUserName(it.loginResult.name)
                    updateUserToken(it.loginResult.token)
                }
                responseAction.postValue(it)
            }
    }

}