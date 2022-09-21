package com.andikas.storyapp.ui.story.home

import androidx.lifecycle.MutableLiveData
import com.andikas.storyapp.base.BaseViewModel
import com.andikas.storyapp.data.repository.StoryRepository
import com.andikas.storyapp.data.repository.UserPreferencesRepository
import com.andikas.storyapp.data.source.remote.response.BaseResponse
import com.andikas.storyapp.data.source.remote.response.story.StoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : BaseViewModel() {

    val userName = MutableLiveData<String>()
    val stories = MutableLiveData<List<StoryResponse>>()

    suspend fun getUserName() {
        userPreferencesRepository.userPreferencesFlow.collect { userName.postValue(it.name) }
    }

    suspend fun userLogout() {
        userPreferencesRepository.clearUserData()
        responseAction.postValue(BaseResponse("", ""))
    }

    suspend fun getStories() {
        storyRepository.getStories { errorMessage.postValue(it) }
            .onStart { isLoading.postValue(true) }
            .onCompletion { isLoading.postValue(false) }
            .collect { it.listStory?.let { list -> stories.postValue(list) } }
    }

}