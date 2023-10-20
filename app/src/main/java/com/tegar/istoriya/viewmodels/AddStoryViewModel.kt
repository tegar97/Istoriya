package com.tegar.istoriya.viewmodels

import androidx.lifecycle.ViewModel
import com.tegar.istoriya.data.repository.StoryRepository
import okhttp3.RequestBody
import java.io.File

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    fun addStory(file: File, description: String, lat: RequestBody? = null,
                 lon: RequestBody? = null) = repository.addStory(file, description,lat,lon)

}