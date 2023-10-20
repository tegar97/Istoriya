package com.tegar.istoriya.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.tegar.istoriya.data.StoryPagingSource
import com.tegar.istoriya.data.api.ResultState
import com.tegar.istoriya.data.api.response.ListStoryItem
import com.tegar.istoriya.data.api.response.StoryDetailResponse
import com.tegar.istoriya.data.api.response.StoryResponse
import com.tegar.istoriya.data.api.response.StoryUploadResponse
import com.tegar.istoriya.data.api.retrofit.ApiService
import com.tegar.istoriya.data.local.entity.StoryEntity
import com.tegar.istoriya.data.local.remotemediator.StoryRemoteMediator
import com.tegar.istoriya.data.local.room.StoryDao
import com.tegar.istoriya.data.local.room.StoryDatabase
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(private  val apiService: ApiService,private  val StoryDatabase : StoryDatabase) {

    @OptIn(ExperimentalPagingApi::class)
    fun getListStory(location : Int = 0) : LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20
            ),
            remoteMediator = StoryRemoteMediator(StoryDatabase,apiService),
            pagingSourceFactory = {
//                StoryPagingSource(apiService)
                StoryDatabase.StoryDao().getAllStory()
            }
        ).liveData

    }

//    fun getListStory(location : Int = 0) : LiveData<ResultState<StoryResponse>> = liveData{
//        emit(ResultState.Loading)
//        try {
//            val storiesResponse =  apiService.getStories(location)
//            val storyList = storiesResponse.listStory.map { story ->
//                StoryEntity(
//                    story.id ?: "",
//                    story.photoUrl ?: "",
//                    story.name ?: "",
//                    story.description,
//                    story.lon,
//                    story.lat,
//                    story.createdAt
//                )
//            }
//            emit(ResultState.Success(storiesResponse))
//            storyDao.clearAll()
//            storyDao.addStory(storyList)
//        }catch (e : HttpException){
//            e.response()?.errorBody()?.string()?.let { error ->
//                val errorBody = e.response()?.errorBody()?.string()
//                val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
//                errorResponse.message?.let { ResultState.Error(it) }?.let { emit(it) }
//            }
//        }
//
//
//    }


    fun getStoriesWithLocation() : LiveData<ResultState<StoryResponse>> = liveData{
        emit(ResultState.Loading)
        try {
            val storiesResponse =  apiService.getStoriesWithLocation()
            val storyList = storiesResponse.listStory.map { story ->
                StoryEntity(
                    story.id ?: "",
                    story.photoUrl ?: "",
                    story.name ?: "",
                    story.description,
                    story.lon,
                    story.lat,
                    story.createdAt
                )
            }
            emit(ResultState.Success(storiesResponse))

        }catch (e : HttpException){
            e.response()?.errorBody()?.string()?.let { error ->
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
                errorResponse.message?.let { ResultState.Error(it) }?.let { emit(it) }
            }
        }


    }


//    fun getStory(storyId : String) : LiveData<ResultState<StoryDetailResponse>> = liveData {
//        emit(ResultState.Loading)
//        try{
//            val detailStoryResponse = apiService.getStory(storyId)
//            emit(ResultState.Success(detailStoryResponse))
//        }catch (e : HttpException){
//            val errorBody = e.response()?.errorBody()?.string()
//            val errorResponse = Gson().fromJson(errorBody, StoryDetailResponse::class.java)
//            emit(ResultState.Error(errorResponse.message))
//
//        }
//    }

    fun getStory(id : String) : LiveData<StoryEntity> {
        return  StoryDatabase.StoryDao().getStory(id)
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
            StoryDatabase: StoryDatabase,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService,StoryDatabase)
            }.also { instance = it }
    }
}