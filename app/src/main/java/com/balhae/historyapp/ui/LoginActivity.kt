package com.balhae.historyapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.balhae.historyapp.R
import com.balhae.historyapp.network.RetrofitClient
import com.balhae.historyapp.network.models.KakaoLoginRequest
import com.balhae.historyapp.network.models.KakaoLoginResponse
import com.balhae.historyapp.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etFakeAccessToken: EditText
    private lateinit var btnKakaoLogin: Button
    private lateinit var btnBackSplash: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etFakeAccessToken = findViewById(R.id.etFakeAccessToken)
        btnKakaoLogin = findViewById(R.id.btnKakaoLogin)
        btnBackSplash = findViewById(R.id.btnBackSplash)

        btnBackSplash.setOnClickListener {
            finish()
        }

        btnKakaoLogin.setOnClickListener {
            // ✅ 여기에 나중에 진짜 카카오 SDK로 받은 토큰 넣으면 됨
            val kakaoAccessToken = etFakeAccessToken.text.toString().ifBlank {
                "TEST_KAKAO_ACCESS_TOKEN"
            }

            loginToServer(kakaoAccessToken)
        }
    }

    private fun loginToServer(kakaoAccessToken: String) {
        val api = RetrofitClient.getApiService(this)

        val request = KakaoLoginRequest(
            accessToken = kakaoAccessToken,
            refreshToken = null // 필요하면 입력
        )

        api.kakaoLogin(request).enqueue(object : Callback<KakaoLoginResponse> {
            override fun onResponse(
                call: Call<KakaoLoginResponse>,
                response: Response<KakaoLoginResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
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
                    Toast.makeText(this@LoginActivity, "로그인 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<KakaoLoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "로그인 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
