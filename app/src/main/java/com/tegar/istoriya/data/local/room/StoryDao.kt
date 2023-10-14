package com.tegar.istoriya.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tegar.istoriya.data.local.entity.StoryEntity

@Dao
interface StoryDao {

    @Query("SELECT * FROM storyEntity ORDER BY createdAt DESC LIMIT 5")
    fun getLatestStory() : List<StoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStory(story: List<StoryEntity>)

    @Query("DELETE FROM storyentity")
    suspend fun clearAll()
}