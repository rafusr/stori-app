package com.andikas.storyapp.ui.story.home

import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.andikas.storyapp.R
import com.andikas.storyapp.base.BaseActivity
import com.andikas.storyapp.data.source.remote.response.story.StoryResponse
import com.andikas.storyapp.databinding.ActivityHomeBinding
import com.andikas.storyapp.ui.auth.login.LoginActivity
import com.andikas.storyapp.ui.story.add.AddStoryActivity
import com.andikas.storyapp.ui.story.detail.StoryDetailActivity
import com.andikas.storyapp.utils.Extension.drawable
import com.andikas.storyapp.utils.Extension.navigateTo
import com.andikas.storyapp.utils.Extension.string
import com.andikas.storyapp.utils.Extension.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    companion object {
        private const val EXTRA_STORY = "extra_story"
    }

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityHomeBinding.inflate(layoutInflater)
    }
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeLoadingNError(viewModel)
        lifecycleScope.launch {
            viewModel.getUserName()
            viewModel.getStories()
        }
        initClickListener()
    }

    override fun observeViewModel() {
        viewModel.responseAction.observe(this) {
            navigateTo<LoginActivity>()
            finishAffinity()
        }
        viewModel.userName.observe(this) {
            binding.tvUserName.text = String.format(string(R.string.greetings_placeholder), it)
        }
        viewModel.stories.observe(this) {
            if (it.isEmpty()) {
                binding.layoutEmpty.visible(true)
                binding.rvStory.visible(false)
            } else {
                binding.layoutEmpty.visible(false)
                binding.rvStory.visible(true)
                initRecycler(it)
            }
        }
    }

    private fun initClickListener() {
        binding.apply {
            btnAddStory.setOnClickListener { navigateTo<AddStoryActivity>() }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                svMain.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                    btnAddStory.apply {
                        when {
                            scrollY > oldScrollY -> {
                                icon = drawable(R.drawable.ic_up)
                                setOnClickListener {
                                    svMain.fling(0)
                                    svMain.smoothScrollTo(0, 0)
                                }
                            }
                            scrollY == 0 -> {
                                icon = drawable(R.drawable.ic_add)
                                setOnClickListener { navigateTo<AddStoryActivity>() }
                            }
                        }
                    }
                }
            }
            tvUserName.apply {
                setOnTouchListener { view, motionEvent ->
                    if (motionEvent.action == MotionEvent.ACTION_UP) {
                        view.performClick()
                        if (motionEvent.rawX >= right - totalPaddingRight) {
                            lifecycleScope.launch { viewModel.userLogout() }
                        }
                    }
                    true
                }
            }
        }
    }

    private fun initRecycler(stories: List<StoryResponse>) {
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = HomeAdapter(stories) { story, bundle ->
                navigateTo<StoryDetailActivity>({
                    putExtra(EXTRA_STORY, story)
                }, bundle)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch { viewModel.getStories() }
    }
}