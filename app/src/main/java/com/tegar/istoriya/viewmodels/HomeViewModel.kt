package com.tegar.istoriya.viewmodels

import androidx.lifecycle.ViewModel
import com.tegar.istoriya.data.repository.StoryRepository

class HomeViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getStories()  = repository.getListStory()
}