package com.andikas.storyapp.ui.story.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andikas.storyapp.data.source.local.entity.StoryEntity
import com.andikas.storyapp.databinding.ItemStoryBinding
import com.andikas.storyapp.utils.Extension.attachImage

class HomeAdapter(
    private val onClick: (story: StoryEntity, optionBundle: Bundle) -> Unit
) : PagingDataAdapter<StoryEntity, HomeAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) holder.bind(story, onClick)
    }

    class ViewHolder(var binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: StoryEntity,
            onClick: (story: StoryEntity, optionBundle: Bundle) -> Unit
        ) {
            with(binding) {
                data.let {
                    ivItemPhoto.attachImage(this@ViewHolder.itemView, it.photo)
                    tvItemName.text = it.name
                }
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@ViewHolder.itemView.context as Activity,
                        androidx.core.util.Pair(tvItemName, "detail_name"),
                        androidx.core.util.Pair(ivItemPhoto, "detail_photo"),
                    )
                root.setOnClickListener {
                    optionsCompat.toBundle()?.let { bundle -> onClick(data, bundle) }
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun getChangePayload(oldItem: StoryEntity, newItem: StoryEntity): Any = Any()
        }
    }
}