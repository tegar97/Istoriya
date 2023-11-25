package com.tegar.istoriya.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.tegar.istoriya.data.local.entity.StoryEntity
import com.tegar.istoriya.data.repository.StoryRepository
import com.tegar.istoriya.utils.DataDummy
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
class DetailStoryViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: StoryRepository

    private lateinit var detailStoryViewModel: DetailStoryViewModel

    @Before
    fun setup(){
        detailStoryViewModel = DetailStoryViewModel(repository)
    }
    @Test
    fun`when user see detail story , the story must return detail data from database`() = runBlocking {
        val storyId = "1"
        val expectedDetailStory = MutableLiveData<StoryEntity>()
        expectedDetailStory.value = dummyDetailStory

        `when`(repository.getStory(storyId)).thenReturn(expectedDetailStory)
        val actualResponse = detailStoryViewModel.getStoryDetail(storyId)
        Mockito.verify(repository).getStory(storyId)

        assertNotNull(actualResponse)
        assertEquals(dummyDetailStory,actualResponse.value)

    }

    companion object{
        private val dummyDetailStory =  DataDummy.generateDetailStory()

    }
}