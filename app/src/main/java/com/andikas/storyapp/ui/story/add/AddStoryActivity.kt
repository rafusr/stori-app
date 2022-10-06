package com.andikas.storyapp.ui.story.add

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.IntentSender
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.andikas.storyapp.BuildConfig
import com.andikas.storyapp.R
import com.andikas.storyapp.base.BaseActivity
import com.andikas.storyapp.databinding.ActivityAddStoryBinding
import com.andikas.storyapp.utils.Extension.color
import com.andikas.storyapp.utils.Extension.createCustomTempFile
import com.andikas.storyapp.utils.Extension.longToast
import com.andikas.storyapp.utils.Extension.reduceFileImage
import com.andikas.storyapp.utils.Extension.selectIntent
import com.andikas.storyapp.utils.Extension.shortToast
import com.andikas.storyapp.utils.Extension.string
import com.andikas.storyapp.utils.Extension.uriToFile
import com.andikas.storyapp.utils.Extension.vectorToBitmap
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class AddStoryActivity : BaseActivity(), OnMapReadyCallback {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityAddStoryBinding.inflate(layoutInflater)
    }
    private val viewModel: AddStoryViewModel by viewModels()

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var isTracked = false

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
    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    Log.i(TAG, "onActivityResult: All location settings are satisfied.")
                    getLastLocation()
                }
                RESULT_CANCELED -> shortToast(string(R.string.error_location_permission))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeLoadingNError(viewModel)
        initClickListener()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_location) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initClickListener() {
        binding.apply {
            tvAddStory.apply {
                setOnTouchListener { view, motionEvent ->
                    if (motionEvent.action == MotionEvent.ACTION_UP) {
                        view.performClick()
                        if (motionEvent.rawX <= totalPaddingLeft) finish()
                    }
                    true
                }
            }
            layoutImage.setOnClickListener {
                requestPermissions { selectIntent({ startGallery() }, { startTakePhoto() }) }
            }
            btnUpdateLocation.setOnClickListener {
                createLocationRequest()
                createLocationCallback()
                startLocationUpdates()
                isTracked = true
            }
            btnAdd.setOnClickListener {
                when {
                    file == null -> longToast(string(R.string.error_image_empty))
                    edAddDescription.text.toString()
                        .isEmpty() -> longToast(string(R.string.error_description_empty))
                    else -> {
                        val description = edAddDescription.text.toString()
                            .toRequestBody("text/plain".toMediaType())
                        var lat: RequestBody? = null
                        var long: RequestBody? = null
                        val latLong = edAddLocation.text.toString()
                        if (latLong.isNotEmpty() && latLong.contains(",")) {
                            val splitLatLong = latLong.split(",")
                            lat = splitLatLong[0].toRequestBody("text/plain".toMediaType())
                            long = splitLatLong[1].toRequestBody("text/plain".toMediaType())
                        }
                        val requestImageFile =
                            file!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                            "photo",
                            file!!.name,
                            requestImageFile
                        )
                        lifecycleScope.launch { viewModel.addNewStory(imageMultipart, description, lat, long) }
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

    @Suppress("DEPRECATION")
    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener { getLastLocation() }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        sendEx.message?.let { shortToast(it) }
                    }
                }
            }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLastLocation() {
        requestLocationPermission {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    binding.edAddLocation.setText("${location.latitude}, ${location.longitude}")
                    val startLocation = LatLng(location.latitude, location.longitude)
                    val addressName = getAddressName(location.latitude, location.longitude)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(startLocation)
                            .title(getString(R.string.last_location))
                            .snippet(addressName)
                            .icon(
                                vectorToBitmap(
                                    R.drawable.ic_logo_primary_mini,
                                    color(R.color.primary_blue)
                                )
                            )
                    )?.showInfoWindow()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 17f))
                } else {
                    shortToast(string(R.string.error_location_not_found))
                }
            }
        }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    Log.d(TAG, "onLocationResult: " + location.latitude + ", " + location.longitude)
                }
            }
        }
    }

    private fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (exception: SecurityException) {
            Log.e(TAG, "Error : " + exception.message)
        }
    }

    private fun stopLocationUpdates() {
        if (isTracked) fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun observeViewModel() {
        viewModel.responseAction.observe(this) { finish() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
        }

        setMapStyle()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    @Suppress("DEPRECATION")
    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this@AddStoryActivity, Locale.getDefault())
        try {
            var list = geocoder.getFromLocation(lat, lon, 1)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(lat, lon, 1) {
                    list = it
                }
            }
            if (list != null && list!!.size != 0) {
                addressName = list!![0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    companion object {
        private const val TAG = "Maps"
    }
}