package com.andikas.storyapp.ui.story.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.andikas.storyapp.DataDummy
import com.andikas.storyapp.MainDispatcherRule
import com.andikas.storyapp.data.repository.StoryRepository
import com.andikas.storyapp.data.source.remote.response.BaseResponse
import com.andikas.storyapp.data.source.remote.response.story.StoryResponse
import com.andikas.storyapp.vo.Location
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryMapsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var storyMapsViewModel: StoryMapsViewModel

    @Before
    fun setUp() {
        storyMapsViewModel = StoryMapsViewModel(storyRepository)
    }

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Test
    fun `when getStoriesWithMaps Should call getStories And Return Success`() = runTest {
        val dummyResponse = DataDummy.generateDummyStoryResponse()
        val expectedResponse: Flow<BaseResponse> = flowOf(dummyResponse)

        `when`(storyRepository.getStories(Location.WITH_LOCATION, 10, null))
            .thenReturn(expectedResponse)

        storyMapsViewModel.getStoriesWithMaps(null)

        verify(storyRepository).getStories(Location.WITH_LOCATION, 10, null)

        storyRepository.getStories(Location.WITH_LOCATION, 10, null).collectLatest {
            Assert.assertFalse(it.error)
            Assert.assertEquals(dummyResponse.message, it.message)
            Assert.assertEquals(
                (dummyResponse.listStory as List<StoryResponse>)[0].description,
                (it.listStory as List<StoryResponse>)[0].description
            )
        }
    }

}