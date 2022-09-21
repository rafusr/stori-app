package com.andikas.storyapp.data.repository

import com.andikas.storyapp.data.source.remote.client.ApiService
import com.andikas.storyapp.data.source.remote.response.BaseResponse
import com.andikas.storyapp.utils.JsonParser.parseTo
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val apiService: ApiService,
    private val ioDispatcher: CoroutineDispatcher
) {

    fun getStories(
        onError: (message: String) -> Unit
    ) = flow {
        apiService.getStories().suspendOnSuccess { emit(data) }
            .onError {
                val responseErrorBody: BaseResponse? =
                    errorBody?.string()?.parseTo(BaseResponse::class.java)
                responseErrorBody?.let { onError(it.message) }
            }
            .onException { message?.let { onError(it) } }
    }.flowOn(ioDispatcher)

    fun addStory(
        file: MultipartBody.Part,
        description: RequestBody,
        onError: (message: String) -> Unit
    ) = flow {
        apiService.addStory(file = file, description = description).suspendOnSuccess { emit(data) }
            .onError {
                val responseErrorBody: BaseResponse? =
                    errorBody?.string()?.parseTo(BaseResponse::class.java)
                responseErrorBody?.let { onError(it.message) }
            }
            .onException { message?.let { onError(it) } }
    }.flowOn(ioDispatcher)

}