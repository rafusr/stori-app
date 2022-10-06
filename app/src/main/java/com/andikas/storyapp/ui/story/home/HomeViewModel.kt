package com.andikas.storyapp.ui.story.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andikas.storyapp.base.BaseViewModel
import com.andikas.storyapp.data.repository.StoryRepository
import com.andikas.storyapp.data.repository.UserPreferencesRepository
import com.andikas.storyapp.data.source.local.entity.StoryEntity
import com.andikas.storyapp.data.source.remote.response.BaseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : BaseViewModel() {

    val userName = MutableLiveData<String>()

    fun pagingStories(): LiveData<PagingData<StoryEntity>> =
        storyRepository.getPagingStories().cachedIn(viewModelScope)

    suspend fun getUserName() {
        userPreferencesRepository.userPreferencesFlow.collect { userName.postValue(it.name) }
    }

    suspend fun userLogout() {
        userPreferencesRepository.clearUserData()
        responseAction.postValue(BaseResponse(false, "success"))
    }

}