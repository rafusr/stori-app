package com.andikas.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.andikas.storyapp.data.mediator.StoryRemoteMediator
import com.andikas.storyapp.data.source.local.entity.StoryEntity
import com.andikas.storyapp.data.source.local.room.StoryDatabase
import com.andikas.storyapp.data.source.remote.client.ApiService
import com.andikas.storyapp.data.source.remote.response.BaseResponse
import com.andikas.storyapp.utils.JsonParser.parseTo
import com.andikas.storyapp.vo.Location
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getStories(
        location: Int? = Location.NO_LOCATION,
        size: Int? = null,
        onError: ((message: String?) -> Unit)?
    ) = flow {
        apiService.getStories(location = location, size = size).suspendOnSuccess { emit(data) }
            .onError {
                val responseErrorBody: BaseResponse? =
                    errorBody?.string()?.parseTo(BaseResponse::class.java)
                responseErrorBody?.let { onError?.invoke(it.message) }
            }
            .onException { message?.let { onError?.invoke(it) } }
    }.flowOn(ioDispatcher)

    fun getPagingStories(): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = { storyDatabase.storyDao().getAllStories() }
        ).liveData
    }

    suspend fun addStory(
        file: MultipartBody.Part?,
        description: RequestBody?,
        lat: RequestBody?,
        lon: RequestBody?,
        onError: ((message: String?) -> Unit)?
    ): Flow<BaseResponse> = flow {
        apiService.addStory(file = file, description = description, lat = lat, lon = lon)
            .suspendOnSuccess { emit(data) }
            .onError {
                val responseErrorBody: BaseResponse? =
                    errorBody?.string()?.parseTo(BaseResponse::class.java)
                responseErrorBody?.let { onError?.invoke(responseErrorBody.message) }
            }
            .onException { onError?.invoke(message) }
    }.flowOn(ioDispatcher)

}