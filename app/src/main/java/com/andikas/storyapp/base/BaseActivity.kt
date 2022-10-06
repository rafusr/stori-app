package com.andikas.storyapp.base

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.andikas.storyapp.R
import com.andikas.storyapp.custom.StoriLoading
import com.andikas.storyapp.utils.Extension.shortToast
import com.andikas.storyapp.utils.Extension.showMessage
import com.andikas.storyapp.utils.Extension.string

abstract class BaseActivity : AppCompatActivity() {

    companion object {
        private val PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        private val LOC_PERMISSION = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private val loadingAnim by lazy(LazyThreadSafetyMode.NONE) { StoriLoading(this) }
    private val getPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { it ->
            val isGranted = it.entries.all { it.value }
            if (!isGranted) shortToast(string(R.string.error_permission))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeViewModel()
    }

    protected abstract fun observeViewModel()

    protected fun observeLoadingNError(viewModel: BaseViewModel) {
        viewModel.isLoading.observe(this) { loadingAnim.showLoading(it) }
        viewModel.errorMessage.observe(this) { showMessage(message = it) }
    }

    protected fun requestPermissions(grantedAction: () -> Unit) {
        if (PERMISSIONS.all {
                ContextCompat.checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }) {
            getPermissionResult.launch(PERMISSIONS)

        } else {
            grantedAction()
        }
    }

    protected fun requestLocationPermission(grantedAction: () -> Unit) {
        if (LOC_PERMISSION.all {
                ContextCompat.checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }) {
            getPermissionResult.launch(LOC_PERMISSION)

        } else {
            grantedAction()
        }
    }

}