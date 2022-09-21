package com.andikas.storyapp.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.andikas.storyapp.base.BaseActivity
import com.andikas.storyapp.databinding.ActivitySplashBinding
import com.andikas.storyapp.ui.auth.login.LoginActivity
import com.andikas.storyapp.ui.story.home.HomeActivity
import com.andikas.storyapp.utils.Extension.navigateTo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        lifecycleScope.launch { viewModel.getUserDetail() }
    }

    override fun observeViewModel() {
        viewModel.userPrefs.observe(this) {
            lifecycleScope.launch {
                if (it.userId.isNullOrEmpty() || it.name.isNullOrEmpty() || it.token.isNullOrEmpty()) {
                    delay(1500)
                    navigateTo<LoginActivity>()
                    finishAffinity()
                } else {
                    delay(1500)
                    navigateTo<HomeActivity>()
                    finishAffinity()
                }
            }
        }
    }
}