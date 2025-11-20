package com.balhae.historyapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.balhae.historyapp.ui.SplashActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // UI 없음, 바로 Splash로 이동
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }
}
