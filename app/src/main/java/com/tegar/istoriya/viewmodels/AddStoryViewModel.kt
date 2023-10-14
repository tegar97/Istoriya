package com.tegar.istoriya.viewmodels

import androidx.lifecycle.ViewModel
import com.tegar.istoriya.data.repository.StoryRepository
import java.io.File

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    fun addStory(file: File, description: String) = repository.addStory(file, description)

}