package com.andikas.storyapp.ui.auth.register

import com.andikas.storyapp.base.BaseViewModel
import com.andikas.storyapp.data.repository.AuthRepository
import com.andikas.storyapp.data.source.remote.response.auth.AuthBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : BaseViewModel() {

    suspend fun userRegister(
        name: String,
        email: String,
        password: String,
        onError: ((message: String?) -> Unit)? = { errorMessage.postValue(it) }
    ) {
        val authBody = AuthBody(name = name, email = email, password = password)
        authRepository.userRegister(authBody, onError)
            .onStart { isLoading.postValue(true) }
            .onCompletion { isLoading.postValue(false) }
            .collect { responseAction.postValue(it) }
    }

}