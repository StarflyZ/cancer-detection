package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.viewmodel.SettingPreferences
import com.dicoding.asclepius.viewmodel.SettingViewModel
import com.dicoding.asclepius.viewmodel.ViewModelFactory
import com.dicoding.asclepius.viewmodel.dataStore
import com.yalantis.ucrop.UCrop
import java.io.File



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null
    private val REQUEST_CODE_PERMISSION = 100
    private lateinit var settingPref: SettingPreferences
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingPref = SettingPreferences.getInstance(dataStore)
        settingViewModel = ViewModelProvider(this, ViewModelFactory(settingPref))[SettingViewModel::class.java]

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSION
            )
        }

        with(binding){
            galleryButton.setOnClickListener { startGallery() }
            ucropButton.setOnClickListener {
                currentImageUri?.let { uri ->
                    startUCrop(uri)
                } ?: run {
                    showToast(getString(R.string.empty_image_warning))
                }
            }
            analyzeButton.setOnClickListener {
                currentImageUri?.let {
                    analyzeImage(it)
                } ?: run {
                    showToast(getString(R.string.empty_image_warning))
                }
            }
        }

        settingViewModel.getThemeSettings().observe(this){ isDarkModeActive ->
            if(isDarkModeActive){
                setTheme(com.google.android.material.R.style.Theme_Material3_Dark)
            }else{
                setTheme(com.google.android.material.R.style.Theme_Material3_Light)
            }
        }
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
            if (uri != null) {
                currentImageUri = uri
                showImage()
            } else {
                showToast("Failed to retrieve image")
            }
        }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage(uri: Uri) {
        moveToResult(uri)
    }

    private fun startUCrop(uri: Uri) {
        val uCrop = UCrop.of(uri, Uri.fromFile(File(cacheDir, "cropped_image")))
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(500, 500)

        uCrop.start(this@MainActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                binding.previewImageView.setImageURI(resultUri)
                moveToResult(resultUri)
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.e("Crop", "Error while cropping: $cropError")
            showToast("Error while cropping image")
        }
    }


    private fun moveToResult(imageUri: Uri) {
        val intent = Intent(this@MainActivity, ResultActivity::class.java)
        currentImageUri?.let {
            intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, imageUri.toString())
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    companion object {
        //const val PREFERENCES = "settings"
    }
}