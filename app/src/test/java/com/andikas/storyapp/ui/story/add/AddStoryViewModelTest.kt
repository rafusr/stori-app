package com.andikas.storyapp.ui.story.add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.andikas.storyapp.DataDummy
import com.andikas.storyapp.MainDispatcherRule
import com.andikas.storyapp.data.repository.StoryRepository
import com.andikas.storyapp.data.source.remote.response.BaseResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
class AddStoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var addStoryViewModel: AddStoryViewModel

    @Before
    fun setUp() {
        addStoryViewModel = AddStoryViewModel(storyRepository)
    }

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Test
    fun `when addNewStory Should call addStory And Return Success`() = runTest {
        val dummyResponse = DataDummy.generateDummySuccessResponse()
        val expectedResponse: Flow<BaseResponse> = flowOf(dummyResponse)

        `when`(storyRepository.addStory(null, null, null, null, null))
            .thenReturn(expectedResponse)

        addStoryViewModel.addNewStory(null, null, null, null, null)

        verify(storyRepository).addStory(null, null, null, null, null)

        storyRepository.addStory(null, null, null, null, null)
            .collectLatest {
                assertFalse(it.error)
                assertEquals(dummyResponse.message, it.message)
            }
    }
}