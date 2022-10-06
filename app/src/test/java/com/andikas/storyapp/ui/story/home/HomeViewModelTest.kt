package com.andikas.storyapp.ui.story.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.andikas.storyapp.DataDummy
import com.andikas.storyapp.MainDispatcherRule
import com.andikas.storyapp.UserPreferences
import com.andikas.storyapp.data.repository.StoryRepository
import com.andikas.storyapp.data.repository.UserPreferencesRepository
import com.andikas.storyapp.data.source.local.entity.StoryEntity
import com.andikas.storyapp.data.source.remote.response.BaseResponse
import com.andikas.storyapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
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
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setUp() {
        homeViewModel = HomeViewModel(storyRepository, userPreferencesRepository)
    }

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Test
    fun `when Get Username Should Not Null and Return Success`() = runTest {
        val dummyName = DataDummy.generateDummyLoginResponse().loginResult?.name
        val dummyResponse = flowOf(UserPreferences.getDefaultInstance())
        userPreferencesRepository.updateUserName(dummyName)
        `when`(userPreferencesRepository.userPreferencesFlow).thenReturn(dummyResponse)
        homeViewModel.getUserName()
        verify(userPreferencesRepository).userPreferencesFlow
        userPreferencesRepository.userPreferencesFlow.collectLatest {
            assertNotNull(it.name)
        }
    }

    @Test
    fun `when Get Story Should Not Null and Return Success`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryEntity()
        val data: PagingData<StoryEntity> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data
        `when`(storyRepository.getPagingStories()).thenReturn(expectedStory)
        val actualStory: PagingData<StoryEntity> = homeViewModel.pagingStories().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = HomeAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory, differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)
    }

    @Test
    fun `when User Logout Should call clearUserData and Return Success`() = runTest {
        val dummyResponse = DataDummy.generateDummySuccessResponse()
        val dummyObject: (response: BaseResponse) -> Unit = {
            assertFalse(it.error)
            assertEquals(dummyResponse.message, it.message)
        }
        `when`(userPreferencesRepository.clearUserData()).thenReturn(dummyObject(dummyResponse))
        homeViewModel.userLogout()
        verify(userPreferencesRepository).clearUserData()
    }

}

class StoryPagingSource : PagingSource<Int, LiveData<List<StoryEntity>>>() {
    companion object {
        fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryEntity>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryEntity>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}