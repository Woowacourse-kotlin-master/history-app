package com.balhae.historyapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.balhae.historyapp.R
import com.balhae.historyapp.network.RetrofitClient
import com.balhae.historyapp.network.models.HeritageRecognizeResponse
import com.balhae.historyapp.util.LoadingDialog
import com.balhae.historyapp.util.TokenManager
import com.balhae.historyapp.util.MultipartUtils
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvPoint: TextView
    private lateinit var tvMemberName: TextView
    private lateinit var ivProfileHeader: ImageView
    private lateinit var btnCamera: Button
    private lateinit var btnHeaderLogout: Button
    private var loadingDialog: LoadingDialog? = null

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

        initializeViews()
        setupListeners()
        loadMemberInfoAndPoint()
    }

    private fun initializeViews() {
        tvPoint = findViewById(R.id.tvPoint)
        tvMemberName = findViewById(R.id.tvMemberName)
        ivProfileHeader = findViewById(R.id.ivProfileHeader)
        btnCamera = findViewById(R.id.btnCamera)
        btnHeaderLogout = findViewById(R.id.btnHeaderLogout)
        loadingDialog = LoadingDialog(this)
    }

    private fun setupListeners() {
        // 카메라 버튼
        btnCamera.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // 프로필 이미지 클릭 → MyPageActivity로 이동
        ivProfileHeader.setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        // 헤더 로그아웃 버튼
        btnHeaderLogout.setOnClickListener {
            TokenManager.clear(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
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
                    tvMemberName.text = body?.userName ?: "사용자"

                    // 프로필 이미지 로드
                    if (!body?.profile.isNullOrEmpty()) {
                        Picasso.get()
                            .load(body?.profile)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .error(R.drawable.ic_profile_placeholder)
                            .into(ivProfileHeader)
                    }
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
        val imagePart = MultipartUtils.createImagePartFromUri(this, uri, "heritageImage")
        if (imagePart == null) {
            Toast.makeText(this, "이미지 변환 실패", Toast.LENGTH_SHORT).show()
            return
        }

        // 로딩 다이얼로그 표시
        loadingDialog?.showDialog("문화재 이미지 분석 중…\n잠시만 기다려주세요")

        val api = RetrofitClient.getApiService(this)
        api.recognizeHeritage(imagePart).enqueue(object : Callback<HeritageRecognizeResponse> {
            override fun onResponse(
                call: Call<HeritageRecognizeResponse>,
                response: Response<HeritageRecognizeResponse>
            ) {
                // 로딩 다이얼로그 닫기
                loadingDialog?.dismiss()

                if (response.isSuccessful) {
                    val body = response.body()
                    Toast.makeText(this@HomeActivity, "문화재 인식 완료!", Toast.LENGTH_SHORT).show()

                    // ✅ 인식 완료 후 마이페이지로 이동
                    val intent = Intent(this@HomeActivity, MyPageActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                } else {
                    Toast.makeText(this@HomeActivity, "토큰이 부족합니다.}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<HeritageRecognizeResponse>, t: Throwable) {
                // 로딩 다이얼로그 닫기
                loadingDialog?.dismiss()
                Toast.makeText(this@HomeActivity, "인식 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}