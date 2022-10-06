package com.andikas.storyapp.data.mediator

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.andikas.storyapp.data.source.local.entity.StoryEntity
import com.andikas.storyapp.data.source.local.room.StoryDatabase
import com.andikas.storyapp.data.source.remote.client.ApiService
import com.andikas.storyapp.data.source.remote.response.BaseResponse
import com.andikas.storyapp.data.source.remote.response.auth.AuthBody
import com.andikas.storyapp.data.source.remote.response.auth.LoginResponse
import com.andikas.storyapp.data.source.remote.response.story.StoryResponse
import com.skydoves.sandwich.ApiResponse
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {

    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent(): Unit = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi
        )
        val pagingState = PagingState<Int, StoryEntity>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }

}

class FakeApiService : ApiService {
    override suspend fun userRegister(authBody: AuthBody?): ApiResponse<BaseResponse> {
        return ApiResponse.Success(
            Response.success(BaseResponse(error = false, message = "User Created"))
        )
    }

    override suspend fun userLogin(authBody: AuthBody?): ApiResponse<BaseResponse> {
        val loginResponse = LoginResponse(
            userId = "dummyUserId",
            name = "dummyUserName",
            token = "dummyUserToken"
        )
        return ApiResponse.Success(
            Response.success(BaseResponse(error = false, message = "success", loginResult = loginResponse))
        )
    }

    override suspend fun getStories(
        page: Int?,
        size: Int?,
        location: Int?
    ): ApiResponse<BaseResponse> {
        val items: MutableList<StoryResponse> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryResponse(
                i.toString(),
                "description + $i",
                "id $i",
                i.toDouble(),
                i.toDouble(),
                "name $i",
                "photo $i"
            )
            items.add(quote)
        }
        return ApiResponse.Success(
            Response.success(BaseResponse(error = false, message = "success", listStory = items))
        )
    }

    override suspend fun getPagingStories(page: Int?, size: Int?): BaseResponse {
        val items: MutableList<StoryResponse> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryResponse(
                i.toString(),
                "description + $i",
                "id $i",
                i.toDouble(),
                i.toDouble(),
                "name $i",
                "photo $i"
            )
            items.add(quote)
        }
        return BaseResponse(error = false, message = "success", listStory = items)
    }

    override suspend fun addStory(
        file: MultipartBody.Part?,
        description: RequestBody?,
        lat: RequestBody?,
        lon: RequestBody?
    ): ApiResponse<BaseResponse> {
        return ApiResponse.Success(Response.success(BaseResponse(error = false, message = "success")))
    }
}
