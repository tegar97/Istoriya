package com.tegar.istoriya.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tegar.istoriya.data.api.response.ListStoryItem
import com.tegar.istoriya.databinding.StoryCardBinding
import com.tegar.istoriya.ui.storydetail.DetailStoryActivity
import com.tegar.istoriya.utilities.withDateFormat

class StoriesAdapter : PagingDataAdapter<ListStoryItem, StoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stories = getItem(position)
        if (stories != null) {
            holder.bind(stories)
        }
    }


    class MyViewHolder(private val binding: StoryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.tvName.text = story.name
            binding.tvCreatedAt.text =   story.createdAt?.withDateFormat()
            binding.tvDescription.text  = story.description
            Glide.with(binding.root.context).load(story.photoUrl).into(binding.imgPhoto)
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_STORY_ID,story.id)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.imgPhoto, "storyImage"),
                        Pair(binding.tvName, "name"),
                        Pair(binding.tvCreatedAt, "createdAt"),

                        Pair(binding.tvDescription, "description"),
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }

        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}