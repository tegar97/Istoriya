package com.tegar.istoriya.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.tegar.istoriya.data.api.ResultState
import com.tegar.istoriya.data.api.response.ListStoryItem
import com.tegar.istoriya.data.api.response.StoryResponse
import com.tegar.istoriya.data.api.response.StoryUploadResponse
import com.tegar.istoriya.data.api.retrofit.ApiService
import com.tegar.istoriya.data.local.entity.StoryEntity
import com.tegar.istoriya.data.local.remotemediator.StoryRemoteMediator
import com.tegar.istoriya.data.local.room.StoryDatabase
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(private  val apiService: ApiService,private  val storyDatabase : StoryDatabase) {

    @OptIn(ExperimentalPagingApi::class)
    fun getListStory() : LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase,apiService),
            pagingSourceFactory = {
                storyDatabase.StoryDao().getAllStory()
            }
        ).liveData

    }



    fun getStoriesWithLocation() : LiveData<ResultState<StoryResponse>> = liveData{
        emit(ResultState.Loading)
        try {
            val storiesResponse =  apiService.getStoriesWithLocation()

            emit(ResultState.Success(storiesResponse))

        }catch (e : HttpException){
            e.response()?.errorBody()?.string()?.let { error ->
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
                errorResponse.message?.let { ResultState.Error(it) }?.let { emit(it) }
            }
        }


    }




    fun getStory(id : String) : LiveData<StoryEntity> {
        return  storyDatabase.StoryDao().getStory(id)
    }

    fun addStory(imageFile : File, description : String, lat: RequestBody? = null,
                 lon: RequestBody? = null) = liveData {
        emit(ResultState.Loading)
        val requestDescription = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {


            val storyUploadResponse = apiService.uploadImage(multipartBody, requestDescription,lat,lon)
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
            storyDatabase: StoryDatabase,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService,storyDatabase)
            }.also { instance = it }
    }
}