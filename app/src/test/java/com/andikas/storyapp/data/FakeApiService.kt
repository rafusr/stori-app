package com.andikas.storyapp.data

import com.andikas.storyapp.DataDummy
import com.andikas.storyapp.data.source.remote.client.ApiService
import com.andikas.storyapp.data.source.remote.response.BaseResponse
import com.andikas.storyapp.data.source.remote.response.auth.AuthBody
import com.skydoves.sandwich.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class FakeApiService : ApiService {

    private val dummySuccessResponse = DataDummy.generateDummySuccessResponse()
    private val dummyStoryResponse = DataDummy.generateDummyStoryResponse()
    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()

    override suspend fun userRegister(authBody: AuthBody?): ApiResponse<BaseResponse> {
        return ApiResponse.Success(Response.success(BaseResponse(false, "success")))
    }

    override suspend fun userLogin(authBody: AuthBody?): ApiResponse<BaseResponse> {
        return ApiResponse.Success(
            Response.success(dummyLoginResponse)
        )
    }

    override suspend fun getStories(
        page: Int?,
        size: Int?,
        location: Int?
    ): ApiResponse<BaseResponse> {
        return ApiResponse.Success(
            Response.success(dummyStoryResponse)
        )
    }

    override suspend fun getPagingStories(page: Int?, size: Int?): BaseResponse {
        return dummyStoryResponse
    }

    override suspend fun addStory(
        file: MultipartBody.Part?,
        description: RequestBody?,
        lat: RequestBody?,
        lon: RequestBody?
    ): ApiResponse<BaseResponse> {
        return ApiResponse.Success(Response.success(dummySuccessResponse))
    }
}