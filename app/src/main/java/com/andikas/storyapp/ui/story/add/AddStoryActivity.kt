package com.andikas.storyapp.ui.story.add

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.andikas.storyapp.BuildConfig
import com.andikas.storyapp.R
import com.andikas.storyapp.base.BaseActivity
import com.andikas.storyapp.databinding.ActivityAddStoryBinding
import com.andikas.storyapp.utils.Extension.createCustomTempFile
import com.andikas.storyapp.utils.Extension.longToast
import com.andikas.storyapp.utils.Extension.reduceFileImage
import com.andikas.storyapp.utils.Extension.selectIntent
import com.andikas.storyapp.utils.Extension.string
import com.andikas.storyapp.utils.Extension.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class AddStoryActivity : BaseActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityAddStoryBinding.inflate(layoutInflater)
    }
    private val viewModel: AddStoryViewModel by viewModels()

    private var file: File? = null
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val result = BitmapFactory.decodeFile(file?.path)
            binding.ivCreatePhotoPreview.setImageBitmap(result).also {
                file = file?.let { file -> reduceFileImage(file) }
            }
        }
    }
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            binding.ivCreatePhotoPreview.setImageURI(selectedImg).also {
                file = reduceFileImage(uriToFile(selectedImg, this@AddStoryActivity))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeLoadingNError(viewModel)
        initClickListener()
    }

    private fun initClickListener() {
        binding.apply {
            tvAddStory.apply {
                setOnTouchListener { view, motionEvent ->
                    if (motionEvent.action == MotionEvent.ACTION_UP) {
                        view.performClick()
                        if (motionEvent.rawX <= totalPaddingLeft) onBackPressed()
                    }
                    true
                }
            }
            layoutImage.setOnClickListener {
                requestPermissions { selectIntent({ startGallery() }, { startTakePhoto() }) }
            }
            edAddDescription.maxLines = Integer.MAX_VALUE
            btnAdd.setOnClickListener {
                when {
                    file == null -> longToast(string(R.string.error_image_empty))
                    edAddDescription.text.toString()
                        .isEmpty() -> longToast(string(R.string.error_description_empty))
                    else -> {
//                        file = reduceFileImage(file!!)
                        val description = edAddDescription.text.toString()
                            .toRequestBody("text/plain".toMediaType())
                        val requestImageFile =
                            file!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                            "photo",
                            file!!.name,
                            requestImageFile
                        )
                        lifecycleScope.launch { viewModel.addNewStory(imageMultipart, description) }
                    }
                }
            }
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                BuildConfig.APPLICATION_ID,
                it
            )
            file = File(it.absolutePath)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    override fun observeViewModel() {
        viewModel.responseAction.observe(this) { finish() }
    }
}