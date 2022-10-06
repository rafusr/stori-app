package com.andikas.storyapp.data.source.remote.response.auth

import com.google.gson.annotations.SerializedName

data class AuthBody(
    @SerializedName("password")
    var password: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    var name: String? = null
)
