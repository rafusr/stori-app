package com.andikas.storyapp.ui.story.detail

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.andikas.storyapp.data.source.remote.response.story.StoryResponse
import com.andikas.storyapp.databinding.ActivityStoryDetailBinding
import com.andikas.storyapp.utils.Extension.attachImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoryDetailActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_STORY = "extra_story"
    }

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityStoryDetailBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val storyDetails = intent.getParcelableExtra<StoryResponse>(EXTRA_STORY)

        if (storyDetails == null) finish()
        else initUI(storyDetails)
    }

    private fun initUI(storyDetails: StoryResponse) {
        binding.apply {
            tvDetailName.apply {
                text = storyDetails.name
                setOnTouchListener { view, motionEvent ->
                    if(motionEvent.action == MotionEvent.ACTION_UP) {
                        view.performClick()
                        if(motionEvent.rawX <= totalPaddingLeft) onBackPressed()
                    }
                    true
                }
            }
            ivDetailPhoto.attachImage(root, storyDetails.photoUrl)
            tvDetailDescription.text = storyDetails.description
        }
    }
}