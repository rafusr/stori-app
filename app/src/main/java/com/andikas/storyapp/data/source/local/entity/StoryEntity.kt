package com.andikas.storyapp.data.source.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey
    val id: String,

    val name: String,

    val description: String,

    val photo: String
) : Parcelable