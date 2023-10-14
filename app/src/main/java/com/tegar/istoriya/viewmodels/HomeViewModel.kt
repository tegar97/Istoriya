package com.tegar.istoriya.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.tegar.istoriya.utilities.Event
import com.tegar.istoriya.data.api.retrofit.ApiConfig
import com.tegar.istoriya.data.api.response.ListStoryItem
import com.tegar.istoriya.data.api.response.StoryResponse
import com.tegar.istoriya.data.pref.UserModel
import com.tegar.istoriya.data.repository.StoryRepository
import com.tegar.istoriya.data.repository.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getStories()  = repository.getListStory()
}