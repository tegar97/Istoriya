package com.tegar.istoriya.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tegar.istoriya.ui.addstory.AddStoryActivity
import com.tegar.istoriya.data.api.response.ListStoryItem
import com.tegar.istoriya.databinding.ActivityHomeBinding
import com.tegar.istoriya.adapters.StoriesAdapter
import com.tegar.istoriya.data.api.ResultState
import com.tegar.istoriya.data.api.response.StoryResponse
import com.tegar.istoriya.ui.setting.SettingActivity
import com.tegar.istoriya.utilities.Utils
import com.tegar.istoriya.viewmodels.HomeViewModel
import com.tegar.istoriya.viewmodels.StoryViewModelFactory

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val homeViewModel by viewModels<HomeViewModel> {
        StoryViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    override fun onResume() {
        super.onResume()
        observeStoryList()
    }

    private fun setupViews() {
        binding.btnSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        binding.btnAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun observeStoryList() {
        homeViewModel.getStories().observe(this) { result ->
            handleStoryResult(result)
        }
    }

    private fun handleStoryResult(result: ResultState<StoryResponse>) {
        when (result) {
            is ResultState.Loading ->  Utils.showLoading(binding.progressBar, true)
            is ResultState.Success -> {
                Utils.showLoading(binding.progressBar,false)
                if (result.data.listStory.isNotEmpty()) {
                    setStoryData(result.data.listStory)
                } else {
                    // Data kosong, tampilkan pesan atau lakukan tindakan lain
                    binding.tvNoData.visibility = View.VISIBLE
                }
                setStoryData(result.data.listStory)
            }

            is ResultState.Error -> {
                Utils.showToast(this,"Error")
                Utils.showLoading(binding.progressBar,false)
            }
        }
    }

    private fun setStoryData(stories: List<ListStoryItem?>?) {
        Log.d("story", stories?.size.toString())
        val adapter = StoriesAdapter()
        adapter.submitList(stories)
        binding.rvStory.adapter = adapter
    }


}
