package com.andikas.storyapp.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.andikas.storyapp.base.BaseActivity
import com.andikas.storyapp.databinding.ActivityLoginBinding
import com.andikas.storyapp.ui.story.home.HomeActivity
import com.andikas.storyapp.ui.auth.register.RegisterActivity
import com.andikas.storyapp.utils.Extension.navigateTo
import com.andikas.storyapp.utils.validator.EmailValidator
import com.andikas.storyapp.utils.validator.PasswordValidator
import com.andikas.storyapp.utils.validator.Validator
import com.andikas.storyapp.utils.validator.ValidatorResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity(), ValidatorResult {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var emailValidator: EmailValidator
    private lateinit var passwordValidator: PasswordValidator
    private lateinit var validators: List<Validator>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeLoadingNError(viewModel)
        playAnimation()
        initClickListener()
        validateForm()
        validateResult()
    }

    override fun observeViewModel() {
        viewModel.responseAction.observe(this) {
            navigateTo<HomeActivity>()
            finishAffinity()
        }
    }

    private fun playAnimation() {
        val root = ObjectAnimator.ofFloat(binding.layoutLogin, View.ALPHA, 1f).setDuration(2000)
        val signup = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(2000)

        AnimatorSet().apply {
            playSequentially(root, signup)
            start()
        }
    }

    private fun initClickListener() {
        binding.apply {
            btnLogin.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.userLogin(
                        edLoginEmail.text.toString(),
                        edLoginPassword.text.toString()
                    )
                }
            }
            btnRegister.setOnClickListener { navigateTo<RegisterActivity>() }
        }
    }

    private fun validateForm() {
        emailValidator = EmailValidator(this, binding.edLoginEmail, binding.tvLoginEmailError)
        passwordValidator = PasswordValidator(this, binding.edLoginPassword, binding.tvLoginPasswordError)
        validators = listOf(emailValidator, passwordValidator)
        validators.forEach { it.validate(this) }
    }

    override fun validateResult() {
        var result = true
        validators.forEach { if (!it.validationResult()) result = false }
        binding.btnLogin.isEnabled = result
    }
}