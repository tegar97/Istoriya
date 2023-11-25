package com.tegar.istoriya.ui.storydetail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.tegar.istoriya.R
import com.tegar.istoriya.data.api.ResultState
import com.tegar.istoriya.data.api.response.Story
import com.tegar.istoriya.data.api.response.StoryDetailResponse
import com.tegar.istoriya.data.local.entity.StoryEntity
import com.tegar.istoriya.databinding.ActivityDetailStoryBinding
import com.tegar.istoriya.utilities.withDateFormat
import com.tegar.istoriya.viewmodels.DetailStoryViewModel
import com.tegar.istoriya.viewmodels.StoryViewModelFactory

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var storyId: String
    private lateinit var binding: ActivityDetailStoryBinding
    private val detailViewModel by viewModels<DetailStoryViewModel> {
        StoryViewModelFactory.getInstance(this)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        extractStoryId()
        Log.d("Story id " ,storyId )

        observeStoryDetail()
        setAction()

    }

    private fun setAction(){
        binding.btnShare.setOnClickListener {
            sendMessage(binding.tvStoryTitle.text.toString() + binding.tvStoryDescription.text.toString() )
        }
    }

    fun sendMessage(message:String) {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp")
        intent.putExtra(Intent.EXTRA_TEXT, message)
        try {
            startActivity(intent)
        } catch (ex: Exception) {
            Toast.makeText(
                this,
                "Please install whatsapp first.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun extractStoryId() {
        storyId = intent.getStringExtra(EXTRA_STORY_ID).orEmpty()
        Log.d("EXTRA_ID", storyId)
    }

    private fun observeStoryDetail() {
        detailViewModel.getStoryDetail(storyId).observe(this){story ->
            setDetailStory(story)
        }
    }



    private fun setDetailStory(story: StoryEntity?) {
        Log.d("FROM Detail story", story?.name.toString())
        binding.tvStoryTitle.text =  getString(R.string.created_by_text,story?.name)
        binding.tvCreatedAt.text = getString(R.string.detail_story_createdAt_dummy,story?.createdAt?.withDateFormat())
        binding.tvStoryDescription.text = story?.desc
        Glide.with(binding.root.context).load(story?.photoUrl).into(binding.storyImage)
    }


    companion object {
        const val EXTRA_STORY_ID: String = "extra_story_id"

    }
}