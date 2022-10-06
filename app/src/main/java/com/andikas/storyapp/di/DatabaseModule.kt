package com.andikas.storyapp.di

import android.app.Application
import com.andikas.storyapp.data.source.local.room.RemoteKeysDao
import com.andikas.storyapp.data.source.local.room.StoryDao
import com.andikas.storyapp.data.source.local.room.StoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): StoryDatabase {
        return StoryDatabase.instance(application)
    }

    @Provides
    @Singleton
    fun provideStoryDao(storyDatabase: StoryDatabase): StoryDao {
        return storyDatabase.storyDao()
    }

    @Provides
    @Singleton
    fun provideRemoteKeysDao(storyDatabase: StoryDatabase): RemoteKeysDao {
        return storyDatabase.remoteKeysDao()
    }

}