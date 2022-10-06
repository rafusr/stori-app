package com.andikas.storyapp

import com.andikas.storyapp.data.source.local.entity.StoryEntity
import com.andikas.storyapp.data.source.remote.response.BaseResponse
import com.andikas.storyapp.data.source.remote.response.auth.AuthBody
import com.andikas.storyapp.data.source.remote.response.auth.LoginResponse
import com.andikas.storyapp.data.source.remote.response.story.StoryResponse

object DataDummy {

    fun generateDummyStoryEntity(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryEntity(
                i.toString(),
                "name + $i",
                "description $i",
                "photo $i",
            )
            items.add(quote)
        }
        return items
    }

    fun generateDummySuccessResponse(): BaseResponse = BaseResponse(false, "success")

    fun generateDummyStoryResponse(): BaseResponse {
        val items: MutableList<StoryResponse> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryResponse(
                i.toString(),
                "name + $i",
                "id + $i",
                i.toDouble(),
                i.toDouble(),
                "name $i",
                "photo $i",
            )
            items.add(quote)
        }
        return BaseResponse(false, "success", listStory = items)
    }

    fun generateDummyLoginResponse(): BaseResponse {
        val loginResponse = LoginResponse(
            "dummyName",
            "dummyToken",
            "dummyId"
        )
        return BaseResponse(false, "success", loginResult = loginResponse)
    }

    fun generateDummyLoginAuthBody(): AuthBody {
        return AuthBody("dummyPassword", "dummyEmail")
    }

    fun generateDummyRegisterAuthBody(): AuthBody {
        return AuthBody("dummyPassword", "dummyEmail", "dummyName")
    }

}