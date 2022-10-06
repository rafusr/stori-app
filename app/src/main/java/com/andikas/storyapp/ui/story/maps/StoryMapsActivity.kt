package com.andikas.storyapp.ui.story.maps

import android.content.res.Resources
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.andikas.storyapp.R
import com.andikas.storyapp.base.BaseActivity
import com.andikas.storyapp.databinding.ActivityStoryMapsBinding
import com.andikas.storyapp.utils.Extension.color
import com.andikas.storyapp.utils.Extension.string
import com.andikas.storyapp.utils.Extension.validateLatitude
import com.andikas.storyapp.utils.Extension.validateLongitude
import com.andikas.storyapp.utils.Extension.vectorToBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class StoryMapsActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryMapsBinding
    private val viewModel: StoryMapsViewModel by viewModels()

    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeLoadingNError(viewModel)
        lifecycleScope.launch { viewModel.getStoriesWithMaps() }

        binding.tvMapsTitle.apply {
            setOnTouchListener { view, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    view.performClick()
                    if (motionEvent.rawX <= totalPaddingLeft) finish()
                }
                true
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun observeViewModel() {
        viewModel.stories.observe(this) {
            it.forEach { story ->
                val latLng = LatLng(story.lat, story.lon)
                val addressName = getAddressName(story.lat, story.lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                        .snippet(addressName)
                        .icon(
                            vectorToBitmap(
                                R.drawable.ic_logo_primary_mini,
                                color(R.color.primary_blue)
                            )
                        )
                )?.showInfoWindow()
                boundsBuilder.include(latLng)
            }
            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
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
                Log.e("Maps", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("Maps", "Can't find style. Error: ", exception)
        }
    }

    @Suppress("DEPRECATION")
    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this@StoryMapsActivity, Locale.getDefault())
        try {
            if (lat.validateLatitude() && lon.validateLongitude()) {
                var list = geocoder.getFromLocation(lat, lon, 1)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(lat, lon, 1) {
                        list = it
                    }
                }
                if (list != null && list!!.size != 0) {
                    addressName = list!![0].getAddressLine(0)
                }
            } else {
                addressName = string(R.string.invalid_location)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }
}