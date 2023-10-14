package com.tegar.istoriya.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.tegar.istoriya.data.api.ResultState
import com.tegar.istoriya.data.api.response.StoryDetailResponse
import com.tegar.istoriya.data.api.response.StoryResponse
import com.tegar.istoriya.data.api.response.StoryUploadResponse
import com.tegar.istoriya.data.api.retrofit.ApiService
import com.tegar.istoriya.data.pref.UserPreference
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(private  val apiService: ApiService, private val userPreference: UserPreference) {

    fun getListStory() : LiveData<ResultState<StoryResponse>> = liveData{
        emit(ResultState.Loading)
        try {
           val storiesResponse =  apiService.getStories()
            emit(ResultState.Success(storiesResponse))

        }catch (e : HttpException){
            e.response()?.errorBody()?.string()?.let { error ->
                val errorBody = e.response()?.errorBody()?.string()
                Log.d("Error" ,errorBody.toString())
                val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
                errorResponse.message?.let { ResultState.Error(it) }?.let { emit(it) }
            }
        }


    }


    fun getStory(storyId : String) : LiveData<ResultState<StoryDetailResponse>> = liveData {
        emit(ResultState.Loading)
        try{
            val detailStoryResponse = apiService.getStory(storyId)
            emit(ResultState.Success(detailStoryResponse))
        }catch (e : HttpException){
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, StoryDetailResponse::class.java)
            emit(ResultState.Error(errorResponse.message))

        }
    }

    fun addStory(imageFile : File, description : String) = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val storyUploadResponse = apiService.uploadImage(multipartBody, requestBody)
            emit(ResultState.Success(storyUploadResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, StoryUploadResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
        }

    }
    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService,userPreference)
            }.also { instance = it }
    }
}