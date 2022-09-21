package com.andikas.storyapp.ui.story.add

import com.andikas.storyapp.base.BaseViewModel
import com.andikas.storyapp.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : BaseViewModel() {

    suspend fun addNewStory(
        file: MultipartBody.Part,
        description: RequestBody
    ) {
        storyRepository.addStory(file, description) { errorMessage.postValue(it) }
            .onStart { isLoading.postValue(true) }
            .onCompletion { isLoading.postValue(false) }
            .collect { responseAction.postValue(it) }
    }

}