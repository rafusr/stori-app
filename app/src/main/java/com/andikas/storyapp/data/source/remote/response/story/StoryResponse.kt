package com.andikas.storyapp.data.source.remote.response.story

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryResponse(
    @SerializedName("createdAt")
    var createdAt: String = "",

    @SerializedName("description")
    var description: String = "",

    @SerializedName("id")
    var id: String = "",

    @SerializedName("lat")
    var lat: Double = 0.0,

    @SerializedName("lon")
    var lon: Double = 0.0,

    @SerializedName("name")
    var name: String = "",

    @SerializedName("photoUrl")
    var photoUrl: String = ""
) : Parcelable