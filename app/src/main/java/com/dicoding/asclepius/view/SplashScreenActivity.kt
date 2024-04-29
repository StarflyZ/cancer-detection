package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dicoding.asclepius.R

class SplashScreenActivity : AppCompatActivity() {
    private var DELAY: Long = 2500
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        window.decorView.postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        },DELAY)
    }
}