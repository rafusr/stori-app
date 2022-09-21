package com.andikas.storyapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StoryApp : Application() {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    external fun baseUrl(): String

}