package com.tegar.istoriya.utils

import com.tegar.istoriya.data.api.response.ListStoryItem
import com.tegar.istoriya.data.api.response.LoginResponse
import com.tegar.istoriya.data.api.response.LoginResult
import com.tegar.istoriya.data.api.response.RegisterResponse
import com.tegar.istoriya.data.api.response.Story
import com.tegar.istoriya.data.api.response.StoryResponse
import com.tegar.istoriya.data.api.response.StoryUploadResponse
import com.tegar.istoriya.data.local.entity.StoryEntity
import java.util.Random
import java.util.UUID

object DataDummy {

    fun generateDummyStory(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        val random = Random()

            for (i in 0..100) {
                val dummyItem = ListStoryItem(
                    photoUrl = "https://example.com/image$i.jpg",
                    createdAt = "2023-10-21T12:00:00",
                    name = "Story $i",
                    description = "This is the description for story $i.",
                    lon = random.nextDouble(),
                    id = UUID.randomUUID().toString(),
                    lat = random.nextDouble()
                )
                items.add(dummyItem)
            }

        return items
    }

    fun generateDummyStoryResponse() : StoryResponse{
        return StoryResponse(
            error = false,
        message =  "Stories fetched successfully",
        listStory =  generateDummyStory()
        )
    }

    fun generateDummyLoginResponse(): LoginResponse {
        val loginResult = LoginResult(
            userId = "user-yj5pc_LARC_AgK61",
            name = "Arif Faizin",
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXlqNXBjX0xBUkNfQWdLNjEiLCJpYXQiOjE2NDE3OTk5NDl9.flEMaQ7zsdYkxuyGbiXjEDXO8kuDTcI__3UjCwt6R_I"
        )

        return LoginResponse(
            loginResult = loginResult,
            error = false,
            message = "success"
        )
    }

    // Register response and  story Response can refactor to have only one based code , butt for now i want to make this simply :)

    fun generaRegisterDummyResponse(): RegisterResponse {
        return RegisterResponse(
            error= false,
            message= "User created"
        )
    }

    fun generateStoryUploadResponse(): StoryUploadResponse {
        return StoryUploadResponse(
            error= false,
            message= "success"
        )
    }

    fun generateDetailStory() : StoryEntity {
        return StoryEntity(
            "1","Title", "Content", "Author"
        )
    }
}