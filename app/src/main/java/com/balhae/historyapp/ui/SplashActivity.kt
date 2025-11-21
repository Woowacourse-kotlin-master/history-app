package com.balhae.historyapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.balhae.historyapp.R
import com.balhae.historyapp.util.TokenManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 2.5초 후 토큰 체크 후 화면 전환
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 2500L)
    }

    private fun navigateToNextScreen() {
        val isLoggedIn = TokenManager.isLoggedIn(this)

        val nextActivityClass = if (isLoggedIn) {
            HomeActivity::class.java
        } else {
            LoginActivity::class.java
        }

        startActivity(Intent(this, nextActivityClass))
        finish()
        // 부드러운 전환 애니메이션
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
