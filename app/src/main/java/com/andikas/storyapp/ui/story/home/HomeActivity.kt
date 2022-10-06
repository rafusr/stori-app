package com.andikas.storyapp.ui.story.home

import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.andikas.storyapp.R
import com.andikas.storyapp.base.BaseActivity
import com.andikas.storyapp.databinding.ActivityHomeBinding
import com.andikas.storyapp.ui.auth.login.LoginActivity
import com.andikas.storyapp.ui.story.add.AddStoryActivity
import com.andikas.storyapp.ui.story.detail.StoryDetailActivity
import com.andikas.storyapp.ui.story.maps.StoryMapsActivity
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

    private val launcherIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { getData() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeLoadingNError(viewModel)
        lifecycleScope.launch { viewModel.getUserName() }
        initClickListener()
        getData()
    }

    override fun observeViewModel() {
        viewModel.responseAction.observe(this) {
            navigateTo<LoginActivity>()
            finishAffinity()
        }
        viewModel.userName.observe(this) {
            binding.tvUserName.text = String.format(string(R.string.greetings_placeholder), it)
        }
    }

    private fun initClickListener() {
        binding.apply {
            btnMapStory.setOnClickListener { launcherIntent.navigateTo<StoryMapsActivity>(this@HomeActivity) }
            btnAddStory.setOnClickListener { launcherIntent.navigateTo<AddStoryActivity>(this@HomeActivity) }
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
                                setOnClickListener {
                                    launcherIntent.navigateTo<AddStoryActivity>(
                                        this@HomeActivity
                                    )
                                }
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

    private fun getData() {
        val homeAdapter = HomeAdapter { story, bundle ->
            navigateTo<StoryDetailActivity>({
                putExtra(EXTRA_STORY, story)
            }, bundle)
        }
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = homeAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { homeAdapter.retry() }
            )
        }
        viewModel.pagingStories().observe(this) {
            homeAdapter.submitData(lifecycle, it)
            if (homeAdapter.snapshot().items.isEmpty()) {
                binding.layoutEmpty.visible(true)
                binding.rvStory.visible(false)
            } else {
                binding.layoutEmpty.visible(false)
                binding.rvStory.visible(true)
            }
        }
    }
}