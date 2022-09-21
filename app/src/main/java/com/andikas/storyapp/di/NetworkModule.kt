package com.andikas.storyapp.di

import android.app.Application
import com.andikas.storyapp.StoryApp
import com.andikas.storyapp.data.repository.UserPreferencesRepository
import com.andikas.storyapp.data.source.remote.client.ApiClient
import com.andikas.storyapp.data.source.remote.client.ApiService
import com.andikas.storyapp.data.source.remote.client.okHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(
        application: Application,
        okHttpClient: OkHttpClient
    ): ApiService = ApiClient(okHttpClient).instance((application as StoryApp).baseUrl())

    @Provides
    fun provideOkHttpClient(
        userPreferencesRepository: UserPreferencesRepository
    ): OkHttpClient = okHttpClient(userPreferencesRepository)
}