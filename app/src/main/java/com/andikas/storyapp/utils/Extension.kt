package com.andikas.storyapp.utils

import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.andikas.storyapp.R
import com.andikas.storyapp.databinding.DialogIntentBinding
import com.andikas.storyapp.databinding.DialogMessageBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object Extension {
    private const val FILENAME_FORMAT = "dd-MMM-yyyy"

    private val timeStamp: String = SimpleDateFormat(
        FILENAME_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())

    fun Context.showMessage(
        message: String,
        @RawRes animResource: Int? = null
    ) = Dialog(this).apply {
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogMessageBinding.inflate(layoutInflater)
        binding.apply {
            if (animResource != null) animView.setAnimation(animResource)
            btnDismiss.setOnClickListener { dismiss() }
            tvMessage.text = message
        }
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        show()
    }

    fun Context.selectIntent(
        actionGallery: () -> Unit,
        actionCamera: () -> Unit
    ) = Dialog(this).apply {
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogIntentBinding.inflate(layoutInflater)
        binding.apply {
            btnGallery.setOnClickListener {
                actionGallery()
                dismiss()
            }
            btnCamera.setOnClickListener {
                actionCamera()
                dismiss()
            }
        }
        setContentView(binding.root)
        window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        show()
    }

    inline fun <reified A : Activity> Context.navigateTo(
        configIntent: Intent.() -> Unit = {},
        bundle: Bundle? = null
    ) {
        startActivity(Intent(this, A::class.java).apply(configIntent), bundle)
    }

    inline fun <reified A : Activity> ActivityResultLauncher<Intent>.navigateTo(context: Context) {
        launch(Intent(context, A::class.java))
    }

    fun Context.string(@StringRes string: Int): String =
        resources.getString(string)

    fun Context.drawable(@DrawableRes drawable: Int): Drawable? =
        ResourcesCompat.getDrawable(resources, drawable, null)

    fun Context.color(@ColorRes color: Int): Int =
        ContextCompat.getColor(this, color)

    fun Context.shortToast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    fun Context.longToast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    fun View.visible(state: Boolean) {
        this.visibility = if (state) View.VISIBLE else View.GONE
    }

    fun ImageView.attachImage(view: View, image: String) {
        Glide.with(view)
            .load(image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.ic_gallery)
            .into(this)
    }

    fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    fun Context.vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
            ?: return BitmapDescriptorFactory.defaultMarker()
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth * 2,
            vectorDrawable.intrinsicHeight * 2,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun String.validatePassword(): Boolean =
        Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,}$").matcher(this).matches()

    fun String.validateEmail(): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun String.validateChar(): Boolean =
        Pattern.compile("^[a-zA-Z ]*$").matcher(this).matches()

    fun Double.validateLatitude(): Boolean =
        (-90..90).contains(this@validateLatitude.toInt())

    fun Double.validateLongitude(): Boolean =
        (-180..180).contains(this@validateLongitude.toInt())

    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

}