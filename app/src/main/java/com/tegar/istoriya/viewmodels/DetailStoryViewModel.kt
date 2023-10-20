package com.tegar.istoriya.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tegar.istoriya.data.local.entity.StoryEntity
import com.tegar.istoriya.data.repository.StoryRepository
import kotlinx.coroutines.launch

class DetailStoryViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getStoryDetail(storyId : String) : LiveData<StoryEntity>   = repository.getStory(storyId)


}