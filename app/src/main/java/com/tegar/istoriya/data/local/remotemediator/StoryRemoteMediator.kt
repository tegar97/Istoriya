package com.tegar.istoriya.data.local.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.tegar.istoriya.data.api.response.ListStoryItem
import com.tegar.istoriya.data.api.retrofit.ApiService
import com.tegar.istoriya.data.local.entity.StoryEntity
import com.tegar.istoriya.data.local.entity.RemoteKeys
import com.tegar.istoriya.data.local.room.StoryDatabase

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(private val database: StoryDatabase,private val apiService: ApiService) : RemoteMediator<Int, ListStoryItem>()  {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH ->{
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try{
            var responseData = apiService.getStories(page,state.config.pageSize)
            Log.d("Response data mediator" ,responseData.toString() )
            val endOfPaginationReached = responseData.listStory.isEmpty()

            database.withTransaction {
                if(loadType == LoadType.REFRESH){
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.StoryDao().clearAll()
                }

                val storyList = responseData.listStory.map { story ->
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
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.listStory.map {
                    it.id?.let { it1 -> RemoteKeys(id = it1, prevKey = prevKey, nextKey = nextKey) }
                }
                database.remoteKeysDao().insertAll(keys)
                Log.d("Story entity" , storyList.toString())
               database.StoryDao().addStory(storyList)

            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        }catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            data.id?.let { database.remoteKeysDao().getRemoteKeysId(it) }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            data.id?.let { database.remoteKeysDao().getRemoteKeysId(it) }
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }
}