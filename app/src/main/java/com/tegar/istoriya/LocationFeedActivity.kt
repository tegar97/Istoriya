package com.tegar.istoriya

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.tegar.istoriya.adapters.StoriesAdapter
import com.tegar.istoriya.data.api.ResultState
import com.tegar.istoriya.data.api.response.ListStoryItem
import com.tegar.istoriya.data.api.response.StoryResponse
import com.tegar.istoriya.databinding.ActivityLocationFeedBinding
import com.tegar.istoriya.utilities.Utils
import com.tegar.istoriya.viewmodels.LocationFeedViewModel
import com.tegar.istoriya.viewmodels.StoryViewModelFactory

class LocationFeedActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityLocationFeedBinding
    private val locationFeedViewModel by viewModels<LocationFeedViewModel> {
        StoryViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocationFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        binding.storyBox.visibility = View.GONE

        val indonesiaLatLng = LatLng(-2.5489, 118.0149)
        val zoomLevel = 1.0f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(indonesiaLatLng, zoomLevel))
        observeStoryList()
        setMapStyle()

    }


    private fun observeStoryList() {
        locationFeedViewModel.getStoryWithLocation().observe(this) { result ->
            handleStoryResult(result)
        }
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
    private fun handleStoryResult(result: ResultState<StoryResponse>) {
        when (result) {
            is ResultState.Loading -> Utils.showLoading(binding.progressBar,true)
            is ResultState.Success -> {
                Utils.showLoading(binding.progressBar,false)
                setStoryData(result.data.listStory)
                mMap.setOnMarkerClickListener(this)
            }

            is ResultState.Error -> {
                Utils.showLoading(binding.progressBar,false)
                Utils.showToast(this, result.error)
            }


        }
    }

    private fun setStoryData(stories: List<ListStoryItem?>?) {
        stories?.forEach { story ->
            if (story != null) {
                val latLng = story.lat?.let { story.lon?.let { it1 -> LatLng(it, it1) } }
                latLng?.let {
                    val markerOptions = MarkerOptions().position(it).title(getString(R.string.created_by_text,story?.name)).icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                    markerOptions.snippet(story.description) // Tambahkan deskripsi tambahan di sini
                    mMap.addMarker(markerOptions)

                }
            }
        }
    }


    override fun onMarkerClick(p0: Marker): Boolean {



        // Handle marker click event
        p0?.let {it
            // Customize this part based on your data model
            binding.storyBox.visibility = View.VISIBLE

            binding.storyTitle.text = it.title
            binding.storyDescription.text = it.snippet
            // Display story information in the box

            }


        // Return 'false' to allow the default behavior (opening the info window)
        // Return 'true' to consume the event and prevent the info window from showing
        return false
    }

    companion object {
        private const val TAG = "MapsActivity"
    }


}