package com.tegar.istoriya.viewmodels

import androidx.lifecycle.ViewModel
import com.tegar.istoriya.data.repository.StoryRepository

class DetailStoryViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getStoryDetail(storyId : String) =  repository.getStory(storyId)
}