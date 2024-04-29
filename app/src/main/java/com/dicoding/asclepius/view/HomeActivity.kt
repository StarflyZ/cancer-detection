package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            ivScan.setOnClickListener {
                intent = Intent(this@HomeActivity, MainActivity::class.java)
                startActivity(intent)
            }
            ivToProfile.setOnClickListener {
                intent = Intent(this@HomeActivity, ProfileActivity::class.java)
                startActivity(intent)
            }
            ivSetting.setOnClickListener {
                intent = Intent(this@HomeActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
    }
}