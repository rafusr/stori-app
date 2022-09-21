package com.andikas.storyapp.ui.story.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.andikas.storyapp.data.source.remote.response.story.StoryResponse
import com.andikas.storyapp.databinding.ItemStoryBinding
import com.andikas.storyapp.utils.Extension.attachImage

class HomeAdapter(
    private val stories: List<StoryResponse>,
    private val onClick: (story: StoryResponse, optionBundle: Bundle) -> Unit
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.ViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeAdapter.ViewHolder, position: Int) {
        val story = stories[position]
        holder.binding.apply {
            ivItemPhoto.attachImage(holder.itemView, story.photoUrl)
            tvItemName.text = story.name
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    androidx.core.util.Pair(tvItemName, "detail_name"),
                    androidx.core.util.Pair(ivItemPhoto, "detail_photo"),
                )
            root.setOnClickListener { optionsCompat.toBundle()?.let { bundle -> onClick(story, bundle) } }
        }
    }

    override fun getItemCount(): Int = stories.size

    inner class ViewHolder(var binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)
}