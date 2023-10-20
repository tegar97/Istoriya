package com.tegar.istoriya.data.api.retrofit

import com.tegar.istoriya.data.api.request.LoginRequest
import com.tegar.istoriya.data.api.request.RegistrationRequest
import com.tegar.istoriya.data.api.response.LoginResponse
import com.tegar.istoriya.data.api.response.RegisterResponse
import com.tegar.istoriya.data.api.response.StoryDetailResponse
import com.tegar.istoriya.data.api.response.StoryResponse
import com.tegar.istoriya.data.api.response.StoryUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @POST("register")
    suspend fun register(
        @Body request: RegistrationRequest
    ): RegisterResponse
    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse


    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
        ): StoryResponse



    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location : Int = 1,

    ): StoryResponse
    @GET("stories/{id}")
    suspend fun getStory(
        @Path("id") id :String
    ): StoryDetailResponse

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): StoryUploadResponse
}