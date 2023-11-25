package com.tegar.istoriya.di

import android.content.Context
import com.tegar.istoriya.data.api.retrofit.ApiConfig
import com.tegar.istoriya.data.local.room.StoryDatabase
import com.tegar.istoriya.data.pref.UserPreference
import com.tegar.istoriya.data.pref.dataStore
import com.tegar.istoriya.data.repository.StoryRepository
import com.tegar.istoriya.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {



    fun provideStoryRepository(context: Context) : StoryRepository{
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first()}
        val database = StoryDatabase.getInstance(context)

        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository.getInstance(apiService,database)
    }





    fun provideAuthRepository(context : Context) : UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()

        return UserRepository.getInstance(apiService,pref)
    }












}