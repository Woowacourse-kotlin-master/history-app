package com.balhae.historyapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.balhae.historyapp.R
import com.balhae.historyapp.network.RetrofitClient
import com.balhae.historyapp.network.models.HeritageRecognizeResponse
import com.balhae.historyapp.util.HeritageRepository
import com.balhae.historyapp.util.TokenManager
import com.balhae.historyapp.util.MultipartUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvPoint: TextView
    private lateinit var tvMemberName: TextView
    private lateinit var btnProfile: ImageButton
    private lateinit var btnCamera: Button
    private lateinit var btnBackToLogin: Button

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                uploadHeritageImage(uri)
            } else {
                Toast.makeText(this, "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tvPoint = findViewById(R.id.tvPoint)
        tvMemberName = findViewById(R.id.tvMemberName)
        btnProfile = findViewById(R.id.btnProfile)
        btnCamera = findViewById(R.id.btnCamera)
        btnBackToLogin = findViewById(R.id.btnHomeBackLogin)

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnBackToLogin.setOnClickListener {
            TokenManager.clear(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnCamera.setOnClickListener {
            // 📷 갤러리에서 이미지 선택
            galleryLauncher.launch("image/*")
        }

        loadMemberInfoAndPoint()
    }

    private fun loadMemberInfoAndPoint() {
        val api = RetrofitClient.getApiService(this)

        // 회원 정보 로드
        api.getMemberInfo().enqueue(object : Callback<com.balhae.historyapp.network.models.MemberResponse> {
            override fun onResponse(
                call: Call<com.balhae.historyapp.network.models.MemberResponse>,
                response: Response<com.balhae.historyapp.network.models.MemberResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    tvMemberName.text = body?.name ?: "사용자"
                }
            }

            override fun onFailure(
                call: Call<com.balhae.historyapp.network.models.MemberResponse>,
                t: Throwable
            ) {
                tvMemberName.text = "사용자"
            }
        })

        // 포인트 로드
        api.getPoint().enqueue(object : Callback<com.balhae.historyapp.network.models.PointResponse> {
            override fun onResponse(
                call: Call<com.balhae.historyapp.network.models.PointResponse>,
                response: Response<com.balhae.historyapp.network.models.PointResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    tvPoint.text = "${body?.point ?: 0}P"
                } else {
                    tvPoint.text = "0P"
                }
            }

            override fun onFailure(
                call: Call<com.balhae.historyapp.network.models.PointResponse>,
                t: Throwable
            ) {
                tvPoint.text = "0P"
            }
        })
    }

    private fun uploadHeritageImage(uri: Uri) {
        val imagePart = MultipartUtils.createImagePartFromUri(this, uri, "image")
        if (imagePart == null) {
            Toast.makeText(this, "이미지 변환 실패", Toast.LENGTH_SHORT).show()
            return
        }

        val api = RetrofitClient.getApiService(this)
        api.recognizeHeritage(imagePart).enqueue(object : Callback<HeritageRecognizeResponse> {
            override fun onResponse(
                call: Call<HeritageRecognizeResponse>,
                response: Response<HeritageRecognizeResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    HeritageRepository.lastRecognized = body?.items ?: emptyList()
                    Toast.makeText(this@HomeActivity, "문화재 인식 완료!", Toast.LENGTH_SHORT).show()

                    // ✅ 인식 완료 후 바로 마이페이지로 유도
                    val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@HomeActivity, "인식 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<HeritageRecognizeResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "인식 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}