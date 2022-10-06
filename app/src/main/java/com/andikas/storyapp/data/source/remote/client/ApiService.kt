package com.andikas.storyapp.data.source.remote.client

import com.andikas.storyapp.data.source.remote.response.BaseResponse
import com.andikas.storyapp.data.source.remote.response.auth.AuthBody
import com.skydoves.sandwich.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    /**
     * Authentication
     */
    @POST("register")
    suspend fun userRegister(@Body authBody: AuthBody?): ApiResponse<BaseResponse>

    @POST("login")
    suspend fun userLogin(@Body authBody: AuthBody?): ApiResponse<BaseResponse>

    /**
     * Stories
     */
    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null,
    ): ApiResponse<BaseResponse>

    @GET("stories")
    suspend fun getPagingStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): BaseResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part file: MultipartBody.Part?,
        @Part("description") description: RequestBody?,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null,
    ): ApiResponse<BaseResponse>

}