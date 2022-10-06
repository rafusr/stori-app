package com.andikas.storyapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.andikas.storyapp.DataDummy
import com.andikas.storyapp.MainDispatcherRule
import com.andikas.storyapp.data.FakeApiService
import com.andikas.storyapp.data.source.remote.client.ApiService
import com.andikas.storyapp.data.source.remote.response.BaseResponse
import com.skydoves.sandwich.getOrElse
import com.skydoves.sandwich.isSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class StoryRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        apiService = FakeApiService()
    }

    @Test
    fun `when getStories Should Not Null`() = runTest {
        val expectedStory = DataDummy.generateDummyStoryResponse().listStory
        val actualStory = apiService.getStories()
            .getOrElse { BaseResponse(false, "success", listStory = listOf()) }
        Assert.assertNotNull(actualStory)
        Assert.assertEquals(expectedStory?.size, actualStory.listStory?.size)
    }

    @Test
    fun `when getPagingStories Should Not Null`() = runTest {
        val expectedStory = DataDummy.generateDummyStoryResponse().listStory
        val actualStory = apiService.getPagingStories().listStory
        Assert.assertNotNull(actualStory)
        Assert.assertEquals(expectedStory?.size, actualStory?.size)
    }

    @Test
    fun `when addStory Should Return Success`() = runTest {
        val addStory = apiService.addStory(null, null, null, null)
        Assert.assertTrue(addStory.isSuccess)
    }

}