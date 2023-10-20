package com.tegar.istoriya.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tegar.istoriya.data.repository.StoryRepository

class HomeViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getStories()  = repository.getListStory().cachedIn(viewModelScope)
}