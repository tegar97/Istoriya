package com.tegar.istoriya.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tegar.istoriya.R
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
        setupToolBar()
        setupViews()
    }

    override fun onResume() {
        super.onResume()
        observeStoryList()
    }

     private fun setupToolBar(){
        val toolbar = binding.toolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)
    }

    private fun setupViews() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        binding.btnAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
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
                    binding.tvNoData.visibility = View.VISIBLE
                }
                setStoryData(result.data.listStory)
            }

            is ResultState.Error -> {
                Utils.showToast(this, result.error)
                Utils.showLoading(binding.progressBar,false)
            }
        }
    }

    private fun setStoryData(stories: List<ListStoryItem?>?) {
        val adapter = StoriesAdapter()
        adapter.submitList(stories)
        binding.rvStory.adapter = adapter
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_setting -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}
