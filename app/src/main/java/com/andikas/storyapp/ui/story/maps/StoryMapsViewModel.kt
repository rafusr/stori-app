package com.andikas.storyapp.ui.story.maps

import androidx.lifecycle.MutableLiveData
import com.andikas.storyapp.base.BaseViewModel
import com.andikas.storyapp.data.repository.StoryRepository
import com.andikas.storyapp.data.source.remote.response.story.StoryResponse
import com.andikas.storyapp.vo.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class StoryMapsViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
) : BaseViewModel() {

    val stories = MutableLiveData<List<StoryResponse>>()

    suspend fun getStoriesWithMaps(
        onError: ((message: String?) -> Unit)? = { errorMessage.postValue(it) }
    ) {
        storyRepository.getStories(Location.WITH_LOCATION, 10, onError)
            .onStart { isLoading.postValue(true) }
            .onCompletion { isLoading.postValue(false) }
            .collect { it.listStory?.let { list -> stories.postValue(list) } }
    }

}