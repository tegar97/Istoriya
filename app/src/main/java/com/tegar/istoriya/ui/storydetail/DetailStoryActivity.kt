package com.tegar.istoriya.ui.storydetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.tegar.istoriya.data.api.ResultState
import com.tegar.istoriya.data.api.response.Story
import com.tegar.istoriya.data.api.response.StoryDetailResponse
import com.tegar.istoriya.databinding.ActivityDetailStoryBinding
import com.tegar.istoriya.viewmodels.DetailStoryViewModel
import com.tegar.istoriya.viewmodels.StoryViewModelFactory

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var storyId: String
    private lateinit var binding: ActivityDetailStoryBinding
    private val detailViewModel by viewModels<DetailStoryViewModel> {
        StoryViewModelFactory.getInstance(this)
    }

    companion object {
        const val EXTRA_STORY_ID: String = "extra_story_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        extractStoryId()
        observeStoryDetail()
    }

    private fun extractStoryId() {
        storyId = intent.getStringExtra(EXTRA_STORY_ID).orEmpty()
        Log.d("EXTRA_ID", storyId)
    }

    private fun observeStoryDetail() {
        detailViewModel.getStoryDetail(storyId).observe(this) { result ->
            handleStoryDetailResult(result)
        }
    }

    private fun handleStoryDetailResult(result: ResultState<StoryDetailResponse?>) {
        when (result) {
            is ResultState.Loading -> {
                Log.d("Loading", "Loading ....")
                showLoading(true)
            }

            is ResultState.Success -> {
                Log.d("Success", "Success ....")
                showLoading(false)
                setDetailStory(result.data?.story)
            }

            is ResultState.Error -> {
                Log.d("Fail", "Fail ....")
                showToast(result.error)
                showLoading(false)
            }
        }
    }

    private fun setDetailStory(story: Story?) {
        Log.d("FROM Detail story", story?.name.toString())
        binding.tvStoryTitle.text = story?.name
        binding.tvStoryDescription.text = story?.description
        Glide.with(binding.root.context).load(story?.photoUrl).into(binding.storyImage)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}