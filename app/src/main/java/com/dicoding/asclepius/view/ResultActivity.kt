package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))

        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.resultImage.setImageURI(it)
        }

        val imageClassifierHelper = ImageClassifierHelper(
            threshold = 0.1f,
            maxResults = 3,
            modelName = "cancer_classification.tflite",
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    Log.d(TAG, "Error: $error")
                    showToast(error)
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    results?.let { displayResult(it) }
                }
            }
        )
        imageClassifierHelper.classifyStaticImage(imageUri)
    }

    private fun displayResult(results: List<Classifications>){
        val firstRes = results[0]
        val label = firstRes.categories[0].label
        val score = firstRes.categories[0].score
        fun Float.formatString(): String{
            return String.format("%.2f", this * 100)
        }
        binding.resultText.text = "$label : ${score.formatString()}%"
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"

        const val TAG = "image"
    }
}