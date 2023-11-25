package com.tegar.istoriya.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.tegar.istoriya.data.api.ResultState
import com.tegar.istoriya.data.api.response.StoryResponse
import com.tegar.istoriya.data.repository.StoryRepository
import com.tegar.istoriya.utils.DataDummy
import com.tegar.istoriya.utils.MainDispatcherRule
import com.tegar.istoriya.utils.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LocationFeedViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()
    private lateinit var locationFeedViewModel : LocationFeedViewModel


    @Mock
    private lateinit var storyRepository: StoryRepository

    @Before
    fun setup(){
        locationFeedViewModel = LocationFeedViewModel(storyRepository)
    }



    @Test
    fun `when get data story should not null , return data and error  must false`() = runBlocking {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val expectedResponse = MutableLiveData<ResultState<StoryResponse>>()
        expectedResponse.value = ResultState.Success(dummyStory)

        `when`(storyRepository.getStoriesWithLocation()).thenReturn(expectedResponse)
        val actualResponse = locationFeedViewModel.getStoryWithLocation().getOrAwaitValue()

        Mockito.verify(storyRepository).getStoriesWithLocation()
        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResultState.Success)
        assertEquals(dummyStory.listStory.size,(actualResponse as ResultState.Success).data.listStory.size) //verify liststory size same as dummy data
        assertEquals(false,(actualResponse as ResultState.Success).data.error) // error must false

    }
    @Test
    fun `when error getData should return error `() = runBlocking {

        val expectedResponse = MutableLiveData<ResultState<StoryResponse>>()
        val errorMessage = "Error"
        expectedResponse.value = ResultState.Error(errorMessage)

        `when`(storyRepository.getStoriesWithLocation()).thenReturn(expectedResponse)
        val actualResponse = locationFeedViewModel.getStoryWithLocation().getOrAwaitValue()

        Mockito.verify(storyRepository).getStoriesWithLocation()
        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResultState.Error)
        assertEquals(errorMessage,(actualResponse as ResultState.Error).error)



    }





}