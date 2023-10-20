package com.tegar.istoriya.ui.addstory

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.tegar.istoriya.R
import com.tegar.istoriya.data.api.ResultState
import com.tegar.istoriya.data.api.response.StoryUploadResponse
import com.tegar.istoriya.databinding.ActivityAddStoryBinding
import com.tegar.istoriya.utilities.Utils
import com.tegar.istoriya.utilities.getImageUri
import com.tegar.istoriya.utilities.reduceFileImage
import com.tegar.istoriya.utilities.uriToFile
import com.tegar.istoriya.viewmodels.AddStoryViewModel
import com.tegar.istoriya.viewmodels.StoryViewModelFactory
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private val addStoryViewModel by viewModels<AddStoryViewModel> {
        StoryViewModelFactory.getInstance(this)
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentImageUri: Uri? = null
    private  var userLocation : Location? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private val requestPermissionLocation =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if     (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    userLocation  = location
                    Log.d("My location" , location.toString())
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLocation.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        binding.cbTrackLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && allPermissionsGranted()) {
                // Your code when checkbox is checked and permissions are granted
                Log.d("IS CHECKED", isChecked.toString())
                getMyLastLocation()
            } else {
                // Your code when checkbox is unchecked or permissions are not granted
                Log.d("IS CHECKED", isChecked.toString())
            }
        }

        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        checkAndSetCheckboxStatus()
    }

    private fun checkAndSetCheckboxStatus() {
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && !checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            binding.cbTrackLocation.isChecked = false
        }
    }
    private fun setupButtons() {
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.btnAdd.setOnClickListener { uploadImage() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                currentImageUri = uri
                showImage()
            } else {
                Utils.showToast(this,"no media selected")
            }
        }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()

            // Collect story location information
            var lat: RequestBody? = null
            var lon: RequestBody? = null

            if (userLocation != null) {
                lat =
                    userLocation?.latitude.toString().toRequestBody("text/plain".toMediaType())
                lon =
                    userLocation?.longitude.toString().toRequestBody("text/plain".toMediaType())
            }

                addStoryViewModel.addStory(imageFile, description,lat,lon).observe(this) { result ->
                    handleAddStoryResult(result)

            }
        } ?:Utils.showToast(this, getString(R.string.empty_image_warning))
    }

    private fun handleAddStoryResult(result: ResultState<StoryUploadResponse>) {
        when (result) {
            is ResultState.Loading -> {
                showLoading(true)
                binding.btnAdd.isEnabled = false
                binding.btnAdd.text = getString(R.string.loading_text)
                Utils.showLoading(binding.progressIndicator,true)

            }
            is ResultState.Success -> {
                showToast(result.data.message)
                binding.btnAdd.isEnabled = true
                binding.btnAdd.text = getString(R.string.upload)
                Utils.showLoading(binding.progressIndicator,false)
                finish()
            }
            is ResultState.Error -> {
                showToast(result.error)
                binding.btnAdd.isEnabled = true
                binding.btnAdd.text = getString(R.string.upload)
                Utils.showLoading(binding.progressIndicator,false)            }


        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
