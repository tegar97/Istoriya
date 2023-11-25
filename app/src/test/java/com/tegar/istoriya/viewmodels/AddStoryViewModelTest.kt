package com.tegar.istoriya.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.tegar.istoriya.data.api.ResultState
import com.tegar.istoriya.data.api.response.LoginResponse
import com.tegar.istoriya.data.api.response.StoryUploadResponse
import com.tegar.istoriya.data.repository.StoryRepository
import com.tegar.istoriya.utils.DataDummy
import com.tegar.istoriya.utils.MainDispatcherRule
import com.tegar.istoriya.utils.getOrAwaitValue
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()
    @Mock
    private lateinit var storyRepository: StoryRepository

    private lateinit var addStoryViewModel: AddStoryViewModel

    @Before
    fun setup(){
        addStoryViewModel = AddStoryViewModel(storyRepository)
    }

    @Test
    fun`Upload story successfully must return message success`() {
        val expectedResponse = MutableLiveData<ResultState<StoryUploadResponse>>()
        expectedResponse.value = ResultState.Success(dummyUploadResponse)

        Mockito.`when`(
            storyRepository.addStory(
                imageFile,
                description,
                lat,
                lon
            )
        ).thenReturn(expectedResponse)

        val actualResponse = addStoryViewModel.addStory(    imageFile,
            description,
            lat,
            lon).getOrAwaitValue()

        Mockito.verify(storyRepository).addStory(
            imageFile,
            description,
            lat,
            lon
        )

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResultState.Success)
        Assert.assertTrue(actualResponse is ResultState.Success)
        Assert.assertEquals(dummyUploadResponse,  (actualResponse as ResultState.Success).data)
        Assert.assertEquals("success",  (actualResponse as ResultState.Success).data.message)
        Assert.assertEquals(false,  (actualResponse as ResultState.Success).data.error)


    }

    @Test
    fun`When upload story fail must return message error `() {
        val expectedResponse = MutableLiveData<ResultState<StoryUploadResponse>>()
        val errorMessage = "Unexpected Error"

        expectedResponse.value = ResultState.Error(errorMessage)

        Mockito.`when`(
            storyRepository.addStory(
                imageFile,
                description,
                lat,
                lon
            )
        ).thenReturn(expectedResponse)

        val actualResponse = addStoryViewModel.addStory(    imageFile,
            description,
            lat,
            lon).getOrAwaitValue()

        Mockito.verify(storyRepository).addStory(
            imageFile,
            description,
            lat,
            lon
        )

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResultState.Error)
        Assert.assertEquals(errorMessage, (actualResponse as ResultState.Error).error) // CHECK ResultState error return error message or nah



    }



    companion object {

        val imagePath = "/path/to/your/image.jpg"
        val imageFile = File(imagePath)

        // Dummy data for other fields
        val description = "This is a dummy image description"
        val lat: RequestBody? = RequestBody.create("text/plain".toMediaTypeOrNull(), "latitude_value")
        val lon: RequestBody? = RequestBody.create("text/plain".toMediaTypeOrNull(), "longitude_value")
        private val dummyUploadResponse = DataDummy.generateStoryUploadResponse()
    }



}