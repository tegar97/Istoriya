package com.tegar.istoriya.data.local.room

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tegar.istoriya.data.api.response.ListStoryItem
import com.tegar.istoriya.data.local.entity.StoryEntity
import retrofit2.Call

@Dao
interface StoryDao {

    @Query("SELECT * FROM storyEntity ORDER BY createdAt DESC LIMIT 5")
    fun getLatestStory() : List<StoryEntity>

    @Query("SELECT * FROM storyEntity")
    fun getAllStory() :  PagingSource<Int,ListStoryItem>


    @Query("SELECT * FROM storyEntity where id=:id")
    fun getStory(id : String) : LiveData<StoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStory(story: List<StoryEntity>)

    @Query("DELETE FROM storyEntity")
    suspend fun clearAll()
}