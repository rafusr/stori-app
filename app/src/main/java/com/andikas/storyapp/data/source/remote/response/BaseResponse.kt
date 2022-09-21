package com.andikas.storyapp.data.source.remote.response

import com.andikas.storyapp.data.source.remote.response.auth.LoginResponse
import com.andikas.storyapp.data.source.remote.response.story.StoryResponse
import com.google.gson.annotations.SerializedName

data class BaseResponse(
    @SerializedName("error")
    val error: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("loginResult")
    val loginResult: LoginResponse? = null,

    @SerializedName("listStory")
    val listStory: List<StoryResponse>? = null
)