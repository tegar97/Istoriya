package com.tegar.istoriya.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.tegar.istoriya.adapters.StoriesAdapter
import com.tegar.istoriya.data.api.response.ListStoryItem
import com.tegar.istoriya.data.repository.StoryRepository
import com.tegar.istoriya.utils.DataDummy
import com.tegar.istoriya.utils.MainDispatcherRule
import com.tegar.istoriya.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()
    private lateinit var homeViewModel: HomeViewModel


    @Mock private lateinit var storyRepository: StoryRepository

    @Before
    fun setup(){
        homeViewModel = HomeViewModel(storyRepository)
    }
    @Test
    fun `when Get Should not null and return data`() = runTest {
        val dummyStory = DataDummy.generateDummyStory()
        val data: PagingData<ListStoryItem> =  StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getListStory()).thenReturn(expectedStory)

        val actualStory: PagingData<ListStoryItem> = homeViewModel.getStories().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)


        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])

    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getListStory()).thenReturn(expectedStory)

        val actualQuote: PagingData<ListStoryItem> = homeViewModel.getStories().getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)
        Assert.assertEquals(0, differ.snapshot().size)


    }

    class StoryPagingSource : PagingSource<Int ,  List<ListStoryItem> >(){
        override fun getRefreshKey(state: PagingState<Int,  List<ListStoryItem> >): Int? {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int,  List<ListStoryItem> > {
            return LoadResult.Page(emptyList(), 0, 1)
        }
        companion object {
            fun snapshot(items: List<ListStoryItem> ): PagingData<ListStoryItem> {
                return PagingData.from(items)
            }
        }

    }

    val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

}