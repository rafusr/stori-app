package com.andikas.storyapp.data.repository

import com.andikas.storyapp.data.source.remote.client.ApiService
import com.andikas.storyapp.data.source.remote.response.BaseResponse
import com.andikas.storyapp.data.source.remote.response.auth.AuthBody
import com.andikas.storyapp.utils.JsonParser.parseTo
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun userRegister(
        authBody: AuthBody,
        onError: ((message: String?) -> Unit)?
    ) = flow {
        apiService.userRegister(authBody).suspendOnSuccess { emit(data) }
            .onError {
                val responseErrorBody: BaseResponse? =
                    errorBody?.string()?.parseTo(BaseResponse::class.java)
                responseErrorBody?.let { onError?.invoke(it.message) }
            }
            .onException { onError?.invoke(message) }
    }.flowOn(ioDispatcher)

    suspend fun userLogin(
        authBody: AuthBody,
        onError: ((message: String?) -> Unit)?
    ) = flow {
        apiService.userLogin(authBody).suspendOnSuccess { emit(data) }
            .onError {
                val responseErrorBody: BaseResponse? =
                    errorBody?.string()?.parseTo(BaseResponse::class.java)
                responseErrorBody?.let { onError?.invoke(it.message) }
            }
            .onException { onError?.invoke(message) }
    }.flowOn(ioDispatcher)

}