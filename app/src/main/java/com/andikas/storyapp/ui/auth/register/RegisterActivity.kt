package com.andikas.storyapp.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.andikas.storyapp.base.BaseActivity
import com.andikas.storyapp.databinding.ActivityRegisterBinding
import com.andikas.storyapp.ui.auth.login.LoginActivity
import com.andikas.storyapp.utils.Extension.navigateTo
import com.andikas.storyapp.utils.validator.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : BaseActivity(), ValidatorResult {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val viewModel: RegisterViewModel by viewModels()

    private lateinit var nameValidator: CharValidator
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
        viewModel.responseAction.observe(this) { navigateTo<LoginActivity>() }
    }

    private fun playAnimation() {
        val root = ObjectAnimator.ofFloat(binding.layoutRegister, View.ALPHA, 1f).setDuration(2000)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(2000)

        AnimatorSet().apply {
            playSequentially(root, login)
            start()
        }
    }

    private fun initClickListener() {
        binding.apply {
            btnRegister.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.userRegister(
                        edRegisterName.text.toString(),
                        edRegisterEmail.text.toString(),
                        edRegisterPassword.text.toString()
                    )
                }
            }
            btnLogin.setOnClickListener {
                navigateTo<LoginActivity>()
                finishAffinity()
            }
        }
    }

    private fun validateForm() {
        nameValidator = CharValidator(this, binding.edRegisterName, binding.tvRegisterNameError)
        emailValidator = EmailValidator(this, binding.edRegisterEmail, binding.tvRegisterEmailError)
        passwordValidator =
            PasswordValidator(this, binding.edRegisterPassword, binding.tvRegisterPasswordError)
        validators = listOf(nameValidator, emailValidator, passwordValidator)
        validators.forEach { it.validate(this) }
    }

    override fun validateResult() {
        var result = true
        validators.forEach { if (!it.validationResult()) result = false }
        binding.btnRegister.isEnabled = result
    }

}