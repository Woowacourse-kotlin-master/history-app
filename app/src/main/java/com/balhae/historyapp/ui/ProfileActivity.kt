package com.balhae.historyapp.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.balhae.historyapp.R
import com.balhae.historyapp.network.RetrofitClient
import com.balhae.historyapp.network.models.MemberResponse
import com.balhae.historyapp.network.models.PointResponse
import com.balhae.historyapp.ui.adapters.HeritageGridAdapter
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnBackHome: ImageButton
    private lateinit var ivProfileImage: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvPointProfile: TextView
    private lateinit var tvHeritageCount: TextView
    private lateinit var rvHeritage: RecyclerView
    private lateinit var adapter: HeritageGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initializeViews()
        setupListeners()
        loadMemberInfo()
        loadPoint()
    }

    private fun initializeViews() {
        btnBackHome = findViewById(R.id.btnProfileBackHome)
        ivProfileImage = findViewById(R.id.ivProfileImage)
        tvName = findViewById(R.id.tvProfileName)
        tvPointProfile = findViewById(R.id.tvProfilePoint)
        tvHeritageCount = findViewById(R.id.tvHeritageCount)
        rvHeritage = findViewById(R.id.rvHeritage)

        adapter = HeritageGridAdapter(emptyList())
        rvHeritage.layoutManager = GridLayoutManager(this, 2)
        rvHeritage.adapter = adapter
    }

    private fun setupListeners() {
        btnBackHome.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    private fun loadMemberInfo() {
        val api = RetrofitClient.getApiService(this)
        api.getMemberInfo().enqueue(object : Callback<MemberResponse> {
            override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    tvName.text = body?.userName ?: "사용자"

                    // 프로필 이미지 로드
                    if (!body?.profile.isNullOrEmpty()) {
                        Picasso.get()
                            .load(body?.profile)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .error(R.drawable.ic_profile_placeholder)
                            .into(ivProfileImage)
                    }

                    // Heritage 목록 업데이트
                    if (body?.heritageDtos != null) {
                        adapter.updateData(body.heritageDtos)
                        tvHeritageCount.text = "${body.heritageDtos.size}개"
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "회원 정보 조회 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "회원 정보 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadPoint() {
        val api = RetrofitClient.getApiService(this)
        api.getPoint().enqueue(object : Callback<PointResponse> {
            override fun onResponse(
                call: Call<PointResponse>,
                response: Response<PointResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    tvPointProfile.text = "${body?.point ?: 0}P"
                } else {
                    tvPointProfile.text = "0P"
                }
            }

            override fun onFailure(call: Call<PointResponse>, t: Throwable) {
                tvPointProfile.text = "0P"
            }
        })
    }
}
