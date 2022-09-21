package com.andikas.storyapp.data.source.remote.response.auth

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("name")
    var name: String = "",

    @SerializedName("token")
    var token: String = "",

    @SerializedName("userId")
    var userId: String = ""
)