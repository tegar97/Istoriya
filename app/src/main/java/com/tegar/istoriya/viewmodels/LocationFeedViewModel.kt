package com.tegar.istoriya.viewmodels

import androidx.lifecycle.ViewModel
import com.tegar.istoriya.data.repository.StoryRepository

class LocationFeedViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getStoryWithLocation()  = repository.getStoriesWithLocation()
}