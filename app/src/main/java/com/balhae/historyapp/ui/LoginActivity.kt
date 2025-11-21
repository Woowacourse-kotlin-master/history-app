package com.balhae.historyapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.balhae.historyapp.R
import com.balhae.historyapp.network.RetrofitClient
import com.balhae.historyapp.network.models.KakaoLoginRequest
import com.balhae.historyapp.network.models.KakaoLoginResponse
import com.balhae.historyapp.util.KakaoLoginManager
import com.balhae.historyapp.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var btnKakaoLogin: Button
    private lateinit var btnBackSplash: Button
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnKakaoLogin = findViewById(R.id.btnKakaoLogin)
        btnBackSplash = findViewById(R.id.btnBackSplash)

        btnBackSplash.setOnClickListener {
            finish()
        }

        btnKakaoLogin.setOnClickListener {
            if (!isLoading) {
                performKakaoLogin()
            }
        }
    }

    private fun performKakaoLogin() {
        isLoading = true
        btnKakaoLogin.isEnabled = false
        Toast.makeText(this, "카카오 로그인 중...", Toast.LENGTH_SHORT).show()

        KakaoLoginManager.login(this, object : KakaoLoginManager.KakaoLoginCallback {
            override fun onLoginSuccess(accessToken: String, refreshToken: String?) {
                // 카카오에서 받은 토큰을 백엔드로 전송
                sendTokenToServer(accessToken, refreshToken)
            }

            override fun onLoginFailure(error: String) {
                isLoading = false
                btnKakaoLogin.isEnabled = true
                Toast.makeText(
                    this@LoginActivity,
                    "카카오 로그인 실패: $error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun sendTokenToServer(kakaoAccessToken: String, kakaoRefreshToken: String?) {
        val api = RetrofitClient.getApiService(this)

        val request = KakaoLoginRequest(
            accessToken = kakaoAccessToken,
            refreshToken = kakaoRefreshToken
        )

        api.kakaoLogin(request).enqueue(object : Callback<KakaoLoginResponse> {
            override fun onResponse(
                call: Call<KakaoLoginResponse>,
                response: Response<KakaoLoginResponse>
            ) {
                isLoading = false
                btnKakaoLogin.isEnabled = true

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        // 백엔드에서 받은 JWT 토큰 저장
                        TokenManager.saveTokens(
                            this@LoginActivity,
                            body.accessToken,
                            body.refreshToken
                        )
                        Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "응답이 비어있음", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "로그인 실패: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<KakaoLoginResponse>, t: Throwable) {
                isLoading = false
                btnKakaoLogin.isEnabled = true
                Toast.makeText(
                    this@LoginActivity,
                    "로그인 오류: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}