package com.tegar.istoriya.data.local.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.tegar.istoriya.data.api.request.LoginRequest
import com.tegar.istoriya.data.api.request.RegistrationRequest
import com.tegar.istoriya.data.api.response.ListStoryItem
import com.tegar.istoriya.data.api.response.LoginResponse
import com.tegar.istoriya.data.api.response.RegisterResponse
import com.tegar.istoriya.data.api.response.StoryDetailResponse
import com.tegar.istoriya.data.api.response.StoryResponse
import com.tegar.istoriya.data.api.response.StoryUploadResponse
import com.tegar.istoriya.data.api.retrofit.ApiService
import com.tegar.istoriya.data.local.room.StoryDatabase
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import java.util.Random
import java.util.UUID
@OptIn(ExperimentalPagingApi::class)

class StoryRemoteMediatorTest {
    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()
    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
        )
        val pagingState = PagingState<Int, ListStoryItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }
    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    val random = Random()
    override suspend fun register(request: RegistrationRequest): RegisterResponse {
        TODO("Not yet implemented")
    }

    override suspend fun login(request: LoginRequest): LoginResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStories(page: Int, size: Int): StoryResponse {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                photoUrl = "https://example.com/image$i.jpg", // Replace with a valid URL or use your own logic for generating URLs
                createdAt = "2023-10-21T12:00:00", // Replace with an actual timestamp
                name = "Story $i",
                description = "This is the description for story $i.",
                lon = random.nextDouble(),
                id = UUID.randomUUID().toString(),
                lat = random.nextDouble()
            )
            items.add(quote)
        }
        val startIndex = (page - 1) * size
        val endIndex = startIndex + size
        val sublist = items.subList(startIndex, if (endIndex > items.size) items.size else endIndex)

        return StoryResponse(sublist)
    }

    override suspend fun getStoriesWithLocation(location: Int): StoryResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStory(id: String): StoryDetailResponse {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImage(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): StoryUploadResponse {
        TODO("Not yet implemented")
    }
}